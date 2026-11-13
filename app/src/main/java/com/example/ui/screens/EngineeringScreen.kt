package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.ElectricTeal
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.RobotViewModel

@Composable
fun EngineeringScreen(
    viewModel: RobotViewModel,
    modifier: Modifier = Modifier
) {
    val diagnostics by viewModel.diagnostics.collectAsState()
    val sensorData by viewModel.sensorData.collectAsState()
    val logs by viewModel.terminalLogs.collectAsState()

    var motorPwm by remember { mutableStateOf(128f) }
    var servoAngle by remember { mutableStateOf(90f) }
    var irLearnMode by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(12.dp)
            .testTag("engineering_screen")
    ) {
        // Screen Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.BugReport,
                    contentDescription = "Engineering Diagnostic Console",
                    tint = StatusYellow,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "ENGINEERING DIAGNOSTIC CONSOLE",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = StatusYellow,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Low-Level Hardware Bus & ESP32 Telemetry",
                        fontSize = 9.sp,
                        color = TextSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Button(
                onClick = { viewModel.runDiagnostics() },
                colors = ButtonDefaults.buttonColors(containerColor = StatusYellow, contentColor = CyberDarkBg),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(32.dp).testTag("run_diagnostics_button")
            ) {
                Text("RUN DIAGS", fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Hardware Sensor Bus Status Matrix
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = "HARDWARE READINESS MATRIX",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DiagBadge("ESP32 Socket", diagnostics.esp32Connected)
                            DiagBadge("Camera Pipeline", diagnostics.cameraReady)
                            DiagBadge("ToF Laser", diagnostics.tofReady)
                            DiagBadge("IR Bus", diagnostics.irReady)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DiagBadge("LDR Sensor", diagnostics.ldrReady)
                            DiagBadge("Wi-Fi Telemetry", diagnostics.wifiConnected)
                            DiagBadge("RAM: ${diagnostics.ramUsageMb}MB", true)
                            DiagBadge("CPU: ${diagnostics.cpuUsagePercent}%", true)
                        }
                    }
                }
            }

            // Low-Level Manual Test Controls (ToF, IR, Motor, Servo)
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "LOW-LEVEL HARDWARE TEST BENCH",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            fontFamily = FontFamily.Monospace
                        )

                        // Manual ToF Reading & IR Test
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.sendCommand("TEST_TOF_PING") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0C2038), contentColor = NeonCyan),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("test_tof_button")
                            ) {
                                Text("PING ToF (${sensorData.tof}cm)", fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            }

                            Button(
                                onClick = { viewModel.sendCommand("IR_TX_0x20DF10EF") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0C2038), contentColor = ElectricTeal),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("test_ir_button")
                            ) {
                                Text("TX TEST IR CODE", fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            }
                        }

                        // Servo Angle Test Slider
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Servo Pan Angle: ${servoAngle.toInt()}°", fontSize = 9.sp, color = TextPrimary, fontFamily = FontFamily.Monospace)
                                Text("Command: SERVO_SET_${servoAngle.toInt()}", fontSize = 8.sp, color = TextSecondary, fontFamily = FontFamily.Monospace)
                            }
                            Slider(
                                value = servoAngle,
                                onValueChange = {
                                    servoAngle = it
                                    viewModel.sendCommand("SERVO_${it.toInt()}")
                                },
                                valueRange = 0f..180f,
                                colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                            )
                        }

                        // Motor PWM Test Slider
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Motor PWM Pulse: ${motorPwm.toInt()} / 255", fontSize = 9.sp, color = TextPrimary, fontFamily = FontFamily.Monospace)
                                Text("Command: PWM_${motorPwm.toInt()}", fontSize = 8.sp, color = TextSecondary, fontFamily = FontFamily.Monospace)
                            }
                            Slider(
                                value = motorPwm,
                                onValueChange = {
                                    motorPwm = it
                                    viewModel.sendCommand("PWM_${it.toInt()}")
                                },
                                valueRange = 0f..255f,
                                colors = SliderDefaults.colors(thumbColor = ElectricTeal, activeTrackColor = ElectricTeal)
                            )
                        }
                    }
                }
            }

            // System Logs & Terminal Console
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Terminal, contentDescription = "Terminal", tint = StatusGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("LIVE SYSTEM TERMINAL LOGS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusGreen, fontFamily = FontFamily.Monospace)
                            }
                            IconButton(onClick = { viewModel.clearLogs() }, modifier = Modifier.size(24.dp)) {
                                Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = "Clear Logs", tint = TextSecondary, modifier = Modifier.size(14.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF02060D))
                                .border(1.dp, CyberGlassBorder, RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(logs) { log ->
                                    Text(
                                        text = log,
                                        fontSize = 8.5.sp,
                                        color = if (log.contains("ERR")) StatusRed else if (log.contains("TX") || log.contains("RX")) ElectricTeal else StatusGreen,
                                        fontFamily = FontFamily.Monospace,
                                        lineHeight = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // System Reset & ESP32 Reboot Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.sendCommand("REBOOT_ESP32") },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusRed.copy(alpha = 0.85f), contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(44.dp).testTag("reboot_esp32_button")
                    ) {
                        Text("RESTART ESP32", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }

                    Button(
                        onClick = { viewModel.testConnection() },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricTeal, contentColor = CyberDarkBg),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(44.dp).testTag("restart_services_button")
                    ) {
                        Text("RESTART SERVICES", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

@Composable
private fun DiagBadge(label: String, isOk: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isOk) StatusGreen.copy(alpha = 0.15f) else StatusRed.copy(alpha = 0.15f))
            .border(1.dp, if (isOk) StatusGreen else StatusRed, RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            color = if (isOk) StatusGreen else StatusRed,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun IconButton(onClick: () -> Unit, modifier: Modifier, content: @Composable () -> Unit) {
    Box(modifier = modifier.clickable { onClick() }, contentAlignment = Alignment.Center) {
        content()
    }
}
