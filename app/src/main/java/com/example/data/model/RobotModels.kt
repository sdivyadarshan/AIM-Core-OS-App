package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.ElectricTeal
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusYellow

enum class RobotEyeState(val color: Color, val label: String) {
    IDLE(NeonCyan, "SYSTEM IDLE"),
    SCANNING(NeonBlue, "SCANNING ENVIRONMENT"),
    THINKING(ElectricTeal, "PROCESSING AI PIPELINE"),
    SEARCHING(NeonCyan, "SEARCHING TARGET"),
    HAPPY(StatusGreen, "ALL SYSTEMS NOMINAL"),
    WARNING(StatusYellow, "WARNING DETECTED"),
    LOW_BATTERY(StatusRed, "CRITICAL BATTERY"),
    DISCONNECTED(StatusRed, "ESP32 DISCONNECTED"),
    OBSTACLE_DETECTED(StatusYellow, "OBSTACLE DETECTED"),
    CHARGING(StatusGreen, "CHARGING POWER"),
    NAVIGATING(ElectricTeal, "NAVIGATING ROUTE"),
    EXPLORING(NeonBlue, "EXPLORING INDOOR MAP"),
    RC_MODE(NeonCyan, "MANUAL RC CONTROL"),
    ASSISTANT_MODE(ElectricTeal, "ROOM ASSISTANT ACTIVE");

    companion object {
        fun fromStatusString(status: String, isMapping: Boolean): RobotEyeState {
            if (status.equals("disconnected", ignoreCase = true)) return DISCONNECTED
            if (isMapping) return EXPLORING
            return when (status.lowercase()) {
                "scanning" -> SCANNING
                "thinking" -> THINKING
                "searching" -> SEARCHING
                "happy" -> HAPPY
                "warning" -> WARNING
                "low_battery" -> LOW_BATTERY
                "charging" -> CHARGING
                "navigating" -> NAVIGATING
                "rc" -> RC_MODE
                "assistant" -> ASSISTANT_MODE
                else -> IDLE
            }
        }
    }
}

enum class ComponentStatus(val label: String, val color: Color) {
    ONLINE("ONLINE", StatusGreen),
    OFFLINE("OFFLINE", StatusRed),
    WARNING("WARN", StatusYellow),
    CALIBRATING("CALIB", NeonBlue)
}

data class SensorData(
    val tof: Int = 72,
    val ldr: Int = 650,
    val ir: String = "0x20DF10EF",
    val battery: Int = 89,
    val status: String = "connected",
    val wifiRssi: Int = -58,
    val latencyMs: Long = 12,
    val cpuUsagePercent: Int = 18,
    val timestamp: Long = System.currentTimeMillis()
) {
    val ldrPercentage: Int
        get() = ((1024 - ldr.coerceIn(0, 1024)) / 1024f * 100).toInt()
}

data class OccupancyGridPoint(
    val x: Float,
    val y: Float,
    val isObstacle: Boolean
)

data class MapData(
    val robotX: Float = 0.5f,
    val robotY: Float = 0.5f,
    val headingAngle: Float = 45f,
    val exploredPercent: Int = 68,
    val obstacles: List<OccupancyGridPoint> = emptyList(),
    val pathTrail: List<Pair<Float, Float>> = emptyList(),
    val estCompletionMinutes: Int = 4,
    val isMappingActive: Boolean = false
)

data class AssistantDevice(
    val id: String,
    val name: String,
    val type: String,
    val isOn: Boolean,
    val confidence: Int,
    val irSupported: Boolean,
    val irCodeOn: String = "0x20DF10EF",
    val irCodeOff: String = "0x20DF40BF"
)

data class RobotSettings(
    val robotName: String = "AIM-BOT 01",
    val esp32Ip: String = "192.168.4.1",
    val esp32Port: Int = 80,
    val isDefaultLauncher: Boolean = false,
    val developerMode: Boolean = true,
    val simulationMode: Boolean = true,
    val speedLimit: Float = 1.0f,
    val soundEffectsEnabled: Boolean = true,
    val aiDetectionEnabled: Boolean = true,
    val isMapping: Boolean = false,
    val isLandscape: Boolean = true
)

data class SystemDiagnostics(
    val appVersion: String = "AIM Core OS v2.1.0",
    val esp32Connected: Boolean = true,
    val cameraReady: Boolean = true,
    val tofReady: Boolean = true,
    val irReady: Boolean = true,
    val ldrReady: Boolean = true,
    val wifiConnected: Boolean = true,
    val ramUsageMb: Long = 340,
    val totalRamMb: Long = 1024,
    val cpuUsagePercent: Int = 22,
    val firmwareVersion: String = "ESP32-AIM-v1.4.2"
)

data class RobotSnapshot(
    val timestamp: Long = System.currentTimeMillis(),
    val tofDistance: Int = 45,
    val detectedObject: String = "Obstacle detected"
)
