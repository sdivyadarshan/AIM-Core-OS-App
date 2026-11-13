package com.example.data.repository

import com.example.data.model.AssistantDevice
import com.example.data.model.ComponentStatus
import com.example.data.model.MapData
import com.example.data.model.OccupancyGridPoint
import com.example.data.model.RobotEyeState
import com.example.data.model.RobotSettings
import com.example.data.model.RobotSnapshot
import com.example.data.model.SensorData
import com.example.data.model.SystemDiagnostics
import com.example.data.network.Esp32NetworkManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RobotRepository(
    private val networkManager: Esp32NetworkManager = Esp32NetworkManager()
) {

    private val _settings = MutableStateFlow(RobotSettings())
    val settings: StateFlow<RobotSettings> = _settings.asStateFlow()

    private val _sensorData = MutableStateFlow(SensorData())
    val sensorData: StateFlow<SensorData> = _sensorData.asStateFlow()

    private val _robotEyeState = MutableStateFlow(RobotEyeState.IDLE)
    val robotEyeState: StateFlow<RobotEyeState> = _robotEyeState.asStateFlow()

    private val _terminalLogs = MutableStateFlow(
        listOf(
            "[SYSTEM] AIM Core OS v2.1.0 Initialized.",
            "[HARDWARE] Galaxy J2 Onboard Computing Engine Active.",
            "[NETWORK] ESP32 Socket Listening on 192.168.4.1:80."
        )
    )
    val terminalLogs: StateFlow<List<String>> = _terminalLogs.asStateFlow()

    private val _snapshots = MutableStateFlow<List<RobotSnapshot>>(emptyList())
    val snapshots: StateFlow<List<RobotSnapshot>> = _snapshots.asStateFlow()

    private val _diagnostics = MutableStateFlow(SystemDiagnostics())
    val diagnostics: StateFlow<SystemDiagnostics> = _diagnostics.asStateFlow()

    private val _mapData = MutableStateFlow(
        MapData(
            robotX = 0.48f,
            robotY = 0.52f,
            headingAngle = 45f,
            exploredPercent = 64,
            obstacles = listOf(
                OccupancyGridPoint(0.2f, 0.3f, true),
                OccupancyGridPoint(0.7f, 0.25f, true),
                OccupancyGridPoint(0.85f, 0.65f, true),
                OccupancyGridPoint(0.35f, 0.8f, true),
                OccupancyGridPoint(0.15f, 0.6f, true)
            ),
            pathTrail = listOf(
                0.1f to 0.1f, 0.2f to 0.25f, 0.35f to 0.4f, 0.48f to 0.52f
            ),
            estCompletionMinutes = 4
        )
    )
    val mapData: StateFlow<MapData> = _mapData.asStateFlow()

    private val _assistantDevices = MutableStateFlow(
        listOf(
            AssistantDevice("dev_tv", "Living Room OLED TV", "TV", true, 96, true),
            AssistantDevice("dev_ac", "Split Climate Control", "AC", true, 92, true),
            AssistantDevice("dev_board", "Interactive Smart Board", "Smart Board", false, 88, true),
            AssistantDevice("dev_light", "Overhead LED Matrix", "Lights", true, 99, false)
        )
    )
    val assistantDevices: StateFlow<List<AssistantDevice>> = _assistantDevices.asStateFlow()

    fun getLiveSensorStream(): Flow<SensorData> {
        return networkManager.getSensorDataStream(
            ipProvider = { _settings.value.esp32Ip },
            portProvider = { _settings.value.esp32Port },
            simulationModeProvider = { _settings.value.simulationMode },
            isMappingProvider = { _settings.value.isMapping }
        )
    }

    fun setEyeState(state: RobotEyeState) {
        _robotEyeState.value = state
    }

    fun updateSensorData(data: SensorData) {
        _sensorData.value = data
        val eyeState = RobotEyeState.fromStatusString(data.status, _settings.value.isMapping)
        if (_robotEyeState.value != RobotEyeState.RC_MODE && _robotEyeState.value != RobotEyeState.ASSISTANT_MODE) {
            _robotEyeState.value = eyeState
        }

        _diagnostics.update { current ->
            current.copy(
                esp32Connected = data.status != "disconnected",
                tofReady = data.tof in 1..999,
                irReady = data.ir.isNotEmpty(),
                ldrReady = data.ldr > 0,
                wifiConnected = data.wifiRssi > -90,
                ramUsageMb = ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)).coerceAtLeast(310),
                totalRamMb = (Runtime.getRuntime().maxMemory() / (1024 * 1024)).coerceAtLeast(1024)
            )
        }
    }

    fun updateSettings(newSettings: RobotSettings) {
        _settings.value = newSettings
        addLog("[CONFIG] IP updated to ${newSettings.esp32Ip}:${newSettings.esp32Port} | DevMode: ${newSettings.developerMode}")
    }

    fun toggleMappingMode() {
        _settings.update { current ->
            val nextMapping = !current.isMapping
            addLog(if (nextMapping) "[NAV] Autonomous Indoor Mapping ENABLED." else "[NAV] Mapping Standby.")
            _mapData.update { m -> m.copy(isMappingActive = nextMapping) }
            current.copy(isMapping = nextMapping)
        }
    }

    fun toggleSimulationMode() {
        _settings.update { current ->
            val nextSim = !current.simulationMode
            addLog(if (nextSim) "[SIM] Hardware Simulation Mode ACTIVATED." else "[NET] Direct Physical ESP32 Connection ENABLED.")
            current.copy(simulationMode = nextSim)
        }
    }

    fun toggleDeveloperMode() {
        _settings.update { current ->
            val nextDev = !current.developerMode
            addLog("[SYS] Developer Options ${if (nextDev) "Unlocked" else "Locked"}.")
            current.copy(developerMode = nextDev)
        }
    }

    fun toggleDevicePower(deviceId: String) {
        _assistantDevices.update { list ->
            list.map { dev ->
                if (dev.id == deviceId) {
                    val nextState = !dev.isOn
                    val cmd = if (nextState) dev.irCodeOn else dev.irCodeOff
                    addLog("[IR TX] Transmitted IR Signal '$cmd' to ${dev.name} [Power -> ${if (nextState) "ON" else "OFF"}]")
                    dev.copy(isOn = nextState)
                } else dev
            }
        }
    }

    fun addLog(entry: String) {
        _terminalLogs.update { current ->
            (current + "[${SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())}] $entry").takeLast(50)
        }
    }

    suspend fun sendCommand(command: String): Result<String> {
        addLog("[TX] Command -> $command")
        val ip = _settings.value.esp32Ip
        val port = _settings.value.esp32Port
        return if (_settings.value.simulationMode) {
            addLog("[RX-SIM] ACK $command [Success]")
            Result.success("SIM ACK: $command")
        } else {
            val res = networkManager.sendRobotCommand(ip, port, command)
            res.onSuccess { addLog("[RX] $it") }
                .onFailure { addLog("[ERR] Send failed: ${it.message}") }
            res
        }
    }

    suspend fun testConnection(): Pair<Boolean, Long> {
        val ip = _settings.value.esp32Ip
        val port = _settings.value.esp32Port
        addLog("[PING] Requesting connection test to $ip:$port...")
        return if (_settings.value.simulationMode) {
            addLog("[PING] Sim Connection OK (6ms)")
            Pair(true, 6L)
        } else {
            val res = networkManager.pingEsp32(ip, port)
            addLog(if (res.first) "[PING] Connection OK (${res.second}ms)" else "[PING] Host Unreachable")
            res
        }
    }

    fun captureSnapshot(tof: Int, objectLabel: String = "Obstacle detected") {
        val snapshot = RobotSnapshot(
            tofDistance = tof,
            detectedObject = "$objectLabel ($tof cm)"
        )
        _snapshots.update { listOf(snapshot) + it }
        addLog("[CAM] Snapshot captured at ToF distance ${tof}cm")
    }

    fun clearLogs() {
        _terminalLogs.value = emptyList()
    }
}
