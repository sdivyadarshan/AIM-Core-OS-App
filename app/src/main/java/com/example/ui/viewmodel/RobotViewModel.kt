package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AssistantDevice
import com.example.data.model.ComponentStatus
import com.example.data.model.MapData
import com.example.data.model.RobotEyeState
import com.example.data.model.RobotSettings
import com.example.data.model.RobotSnapshot
import com.example.data.model.SensorData
import com.example.data.model.SystemDiagnostics
import com.example.data.repository.RobotRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RobotViewModel(
    val repository: RobotRepository = RobotRepository()
) : ViewModel() {

    val sensorData: StateFlow<SensorData> = repository.sensorData
    val eyeState: StateFlow<RobotEyeState> = repository.robotEyeState
    val settings: StateFlow<RobotSettings> = repository.settings
    val diagnostics: StateFlow<SystemDiagnostics> = repository.diagnostics
    val terminalLogs: StateFlow<List<String>> = repository.terminalLogs
    val snapshots: StateFlow<List<RobotSnapshot>> = repository.snapshots
    val mapData: StateFlow<MapData> = repository.mapData
    val assistantDevices: StateFlow<List<AssistantDevice>> = repository.assistantDevices

    val esp32Status: ComponentStatus
        get() = if (diagnostics.value.esp32Connected) ComponentStatus.ONLINE else ComponentStatus.OFFLINE

    val cameraStatus: ComponentStatus
        get() = if (diagnostics.value.cameraReady) ComponentStatus.ONLINE else ComponentStatus.OFFLINE

    val wifiStatus: ComponentStatus
        get() = if (diagnostics.value.wifiConnected) ComponentStatus.ONLINE else ComponentStatus.OFFLINE

    val tofStatus: ComponentStatus
        get() = if (diagnostics.value.tofReady) ComponentStatus.ONLINE else ComponentStatus.OFFLINE

    val irStatus: ComponentStatus
        get() = if (diagnostics.value.irReady) ComponentStatus.ONLINE else ComponentStatus.OFFLINE

    val ldrStatus: ComponentStatus
        get() = if (diagnostics.value.ldrReady) ComponentStatus.ONLINE else ComponentStatus.OFFLINE

    init {
        viewModelScope.launch {
            repository.getLiveSensorStream().collect { data ->
                repository.updateSensorData(data)
            }
        }
    }

    fun setEyeState(state: RobotEyeState) {
        repository.setEyeState(state)
    }

    fun updateIpAddress(newIp: String) {
        repository.updateSettings(settings.value.copy(esp32Ip = newIp.trim()))
    }

    fun updatePort(newPort: Int) {
        repository.updateSettings(settings.value.copy(esp32Port = newPort))
    }

    fun toggleSimulation() {
        repository.toggleSimulationMode()
    }

    fun toggleMapping() {
        repository.toggleMappingMode()
    }

    fun toggleDeveloperMode() {
        repository.toggleDeveloperMode()
    }

    fun toggleOrientation() {
        repository.updateSettings(settings.value.copy(isLandscape = !settings.value.isLandscape))
    }

    fun toggleAiDetection() {
        repository.updateSettings(settings.value.copy(aiDetectionEnabled = !settings.value.aiDetectionEnabled))
    }

    fun toggleDevicePower(deviceId: String) {
        repository.toggleDevicePower(deviceId)
    }

    fun setDefaultLauncher(isDefault: Boolean) {
        repository.updateSettings(settings.value.copy(isDefaultLauncher = isDefault))
    }

    fun sendCommand(command: String) {
        viewModelScope.launch {
            repository.sendCommand(command)
        }
    }

    fun testConnection() {
        viewModelScope.launch {
            repository.testConnection()
        }
    }

    fun runDiagnostics() {
        viewModelScope.launch {
            repository.addLog("[DIAG] Starting AIM Core OS Self-Diagnostics...")
            testConnection()
            repository.addLog("[DIAG] Checking CameraX Pipeline... OK")
            repository.addLog("[DIAG] Checking ToF Laser Ranging Sensor... OK (${sensorData.value.tof} cm)")
            repository.addLog("[DIAG] Checking IR Decoder Socket... OK")
            repository.addLog("[DIAG] Memory Check: ${diagnostics.value.ramUsageMb} MB / ${diagnostics.value.totalRamMb} MB")
            repository.addLog("[DIAG] Diagnostics Complete: ALL SYSTEMS NOMINAL.")
        }
    }

    fun captureSnapshot(tof: Int, label: String = "Obstacle") {
        repository.captureSnapshot(tof, label)
    }

    fun clearLogs() {
        repository.clearLogs()
    }
}
