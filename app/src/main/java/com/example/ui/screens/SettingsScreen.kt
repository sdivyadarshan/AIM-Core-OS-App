package com.example.ui.screens

import android.content.Intent
import android.provider.Settings as AndroidSettings
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.theme.CyberDarkBg
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.ElectricTeal
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.RobotViewModel
import com.example.utils.TtsManager

@Composable
fun SettingsScreen(
    viewModel: RobotViewModel,
    ttsManager: TtsManager? = null,
    onNavigateToAimbotGuide: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()
    val diagnostics by viewModel.diagnostics.collectAsState()

    var ipInput by remember(settings.esp32Ip) { mutableStateOf(settings.esp32Ip) }
    var portInput by remember(settings.esp32Port) { mutableStateOf(settings.esp32Port.toString()) }
    var customSpeechInput by remember { mutableStateOf("System online and ready for action.") }
    var isMuted by remember { mutableStateOf(ttsManager?.isMuted() ?: false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(12.dp)
            .testTag("settings_screen")
    ) {
        // Screen Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = NeonCyan,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "ROBOT OPERATING SYSTEM CONFIG",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Hardware Bus & Socket Communication Setup",
                        fontSize = 9.sp,
                        color = TextSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Text-to-Speech Vocalizer Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = ElectricTeal,
                    borderWidth = 1.dp
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.RecordVoiceOver,
                                    contentDescription = null,
                                    tint = ElectricTeal,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "TEXT-TO-SPEECH VOCALIZER",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ElectricTeal,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "Status announcements & voice synthesizer",
                                        fontSize = 8.5.sp,
                                        color = TextSecondary,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    ttsManager?.toggleMute()?.let { isMute ->
                                        isMuted = isMute
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isMuted) StatusRed.copy(alpha = 0.2f) else StatusGreen.copy(alpha = 0.2f),
                                    contentColor = if (isMuted) StatusRed else StatusGreen
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isMuted) "MUTED" else "ACTIVE",
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        // Quick Status Vocalization Preset Chips
                        Text(
                            text = "PRESET STATUS ANNOUNCEMENTS:",
                            fontSize = 8.5.sp,
                            color = TextSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )

                        val presetPhrases = listOf(
                            "Hello buddy...",
                            "System ready",
                            "Battery low",
                            "Fire mode activated",
                            "Obstacle detected!"
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            presetPhrases.take(3).forEach { phrase ->
                                Button(
                                    onClick = { ttsManager?.speak(phrase) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF0D253E),
                                        contentColor = NeonCyan
                                    ),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.weight(1f).height(30.dp)
                                ) {
                                    Text(
                                        text = phrase,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            presetPhrases.drop(3).forEach { phrase ->
                                Button(
                                    onClick = { ttsManager?.speak(phrase) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF0D253E),
                                        contentColor = NeonCyan
                                    ),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.weight(1f).height(30.dp)
                                ) {
                                    Text(
                                        text = phrase,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        // Custom Vocalize Field
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = customSpeechInput,
                                onValueChange = { customSpeechInput = it },
                                label = { Text("CUSTOM SPEECH", fontSize = 8.sp, color = TextSecondary) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElectricTeal,
                                    unfocusedBorderColor = CyberGlassBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )

                            Button(
                                onClick = { ttsManager?.speak(customSpeechInput) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricTeal,
                                    contentColor = CyberDarkBg
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(50.dp)
                            ) {
                                Text(
                                    text = "SPEAK",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }

            // Understanding AIM-Bot Expressive Eye Guide Card
            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToAimbotGuide?.invoke() },
                    borderColor = NeonCyan,
                    borderWidth = 1.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "UNDERSTANDING AIM-BOT",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "Eye movements, scanning, warning & expressive meanings",
                                    fontSize = 8.5.sp,
                                    color = NeonCyan,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Open Guide",
                            tint = NeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Default Launcher Configuration Card
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("DEFAULT HOME LAUNCHER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Monospace)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (settings.isDefaultLauncher) "AIM Core OS is set as default system HOME" else "Launch AIM Core OS automatically upon boot",
                                fontSize = 9.sp,
                                color = TextSecondary,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.setDefaultLauncher(true)
                                try {
                                    val intent = Intent(AndroidSettings.ACTION_HOME_SETTINGS)
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                                        addCategory(Intent.CATEGORY_HOME)
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    context.startActivity(homeIntent)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = CyberDarkBg),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("set_default_launcher_button")
                        ) {
                            Text(if (settings.isDefaultLauncher) "ACTIVE" else "CONFIGURE", fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }

            // ESP32 Socket Communication Setup
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.DeveloperBoard, contentDescription = null, tint = ElectricTeal, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ESP32 HARDWARE SOCKET CONFIG", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElectricTeal, fontFamily = FontFamily.Monospace)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = ipInput,
                                onValueChange = {
                                    ipInput = it
                                    viewModel.updateIpAddress(it)
                                },
                                label = { Text("ESP32 IP", fontSize = 9.sp, color = TextSecondary) },
                                modifier = Modifier.weight(2f).testTag("ip_input_field"),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonCyan,
                                    unfocusedBorderColor = CyberGlassBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )

                            OutlinedTextField(
                                value = portInput,
                                onValueChange = {
                                    portInput = it
                                    it.toIntOrNull()?.let { p -> viewModel.updatePort(p) }
                                },
                                label = { Text("PORT", fontSize = 9.sp, color = TextSecondary) },
                                modifier = Modifier.weight(1f).testTag("port_input_field"),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonCyan,
                                    unfocusedBorderColor = CyberGlassBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                        }

                        Button(
                            onClick = { viewModel.testConnection() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0C2038), contentColor = NeonCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(38.dp).testTag("test_esp32_connection_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.NetworkCheck, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("PING TEST CONNECTION", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }

            // Toggles: Orientation, Simulation Mode & Developer Mode
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Screen Orientation", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Monospace)
                                Text(
                                    text = if (settings.isLandscape) "LANDSCAPE MODE (Default)" else "PORTRAIT MODE",
                                    fontSize = 9.sp,
                                    color = if (settings.isLandscape) NeonCyan else ElectricTeal,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Switch(
                                checked = settings.isLandscape,
                                onCheckedChange = { viewModel.toggleOrientation() },
                                colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = Color(0xFF0E2540)),
                                modifier = Modifier.testTag("orientation_switch")
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Hardware Simulation Mode", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Monospace)
                                Text("Simulate ESP32 telemetry & motor responses", fontSize = 9.sp, color = TextSecondary, fontFamily = FontFamily.Monospace)
                            }
                            Switch(
                                checked = settings.simulationMode,
                                onCheckedChange = { viewModel.toggleSimulation() },
                                colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = Color(0xFF0E2540)),
                                modifier = Modifier.testTag("simulation_mode_switch")
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Developer Options", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Monospace)
                                Text("Unlock diagnostic engineering console & logs", fontSize = 9.sp, color = TextSecondary, fontFamily = FontFamily.Monospace)
                            }
                            Switch(
                                checked = settings.developerMode,
                                onCheckedChange = { viewModel.toggleDeveloperMode() },
                                colors = SwitchDefaults.colors(checkedThumbColor = ElectricTeal, checkedTrackColor = Color(0xFF0E2540)),
                                modifier = Modifier.testTag("developer_mode_switch")
                            )
                        }
                    }
                }
            }

            // About AIM-Bot by HANDD Innovs
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("AIM-BOT BY HANDD INNOVS", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = NeonCyan, fontFamily = FontFamily.Monospace)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "System: AIM Core OS v2.1.0", fontSize = 10.sp, color = TextPrimary, fontFamily = FontFamily.Monospace)
                        Text(text = "Target Platform: Galaxy J2 (2018) Onboard Robot Computer", fontSize = 9.sp, color = TextSecondary, fontFamily = FontFamily.Monospace)
                        Text(text = "Kernel Subsystem: ESP32 Socket Bridge | SLAM Indoor Mapping", fontSize = 9.sp, color = TextSecondary, fontFamily = FontFamily.Monospace)
                        Text(text = "Firmware: ${diagnostics.firmwareVersion}", fontSize = 9.sp, color = TextSecondary, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}
