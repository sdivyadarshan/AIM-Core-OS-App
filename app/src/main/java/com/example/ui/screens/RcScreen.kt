package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
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
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

@Composable
fun RcScreen(
    viewModel: RobotViewModel,
    modifier: Modifier = Modifier
) {
    val sensorData by viewModel.sensorData.collectAsState()
    val settings by viewModel.settings.collectAsState()

    var speedMultiplier by remember { mutableStateOf(1.0f) }
    var joystickDirectionText by remember { mutableStateOf("STOPPED") }
    var headlightsOn by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(12.dp)
            .testTag("rc_screen")
    ) {
        // Screen Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Games,
                    contentDescription = "RC Control Mode",
                    tint = NeonCyan,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "MANUAL RC TELE-OPERATION",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Direct ESP32 Motor Bus | Latency: ${sensorData.latencyMs}ms",
                        fontSize = 10.sp,
                        color = TextSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Top Action Bar: Speed Multiplier Selector & Headlights & Horn
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Speed Modes (0.5x, 1.0x, 1.5x)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("SPEED:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondary, fontFamily = FontFamily.Monospace)
                    listOf(0.5f, 1.0f, 1.5f).forEach { scale ->
                        val selected = speedMultiplier == scale
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (selected) NeonCyan else Color(0xFF091628))
                                .clickable { speedMultiplier = scale }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("speed_mode_${scale}x")
                        ) {
                            Text(
                                text = "${scale}x",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) CyberDarkBg else TextPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Headlights & Horn Action Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (headlightsOn) StatusYellow.copy(alpha = 0.25f) else Color(0xFF091628))
                            .border(1.dp, if (headlightsOn) StatusYellow else CyberGlassBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                headlightsOn = !headlightsOn
                                viewModel.sendCommand(if (headlightsOn) "LIGHTS_ON" else "LIGHTS_OFF")
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("headlights_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.FlashOn, contentDescription = "Lights", tint = if (headlightsOn) StatusYellow else TextSecondary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("LIGHTS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (headlightsOn) StatusYellow else TextSecondary, fontFamily = FontFamily.Monospace)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(NeonCyan.copy(alpha = 0.2f))
                            .border(1.dp, NeonCyan, RoundedCornerShape(8.dp))
                            .clickable { viewModel.sendCommand("HORN_BEEP") }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("horn_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Horn", tint = NeonCyan, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("HORN", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NeonCyan, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // CENTER: INTERACTIVE TOUCH JOYSTICK & HAZARD EMERGENCY STOP
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Real-time Direction Indicator
                Text(
                    text = "COMMAND: $joystickDirectionText",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeonCyan,
                    letterSpacing = 1.2.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                InteractiveTouchJoystick(
                    onDirectionChanged = { dir, commandStr ->
                        joystickDirectionText = dir
                        viewModel.sendCommand(commandStr)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // EMERGENCY STOP HAZARD BUTTON
        Button(
            onClick = {
                joystickDirectionText = "EMERGENCY STOPPED"
                viewModel.sendCommand("EMERGENCY_STOP")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("emergency_stop_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = StatusRed,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Warning, contentDescription = "E-Stop", modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "EMERGENCY HAZARD STOP",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // IR QUICK ACTION CONTROLS PANEL
        Text(
            text = "IR TRANSMITTER QUICK COMMANDS",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 1.2.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IrQuickButton("TV POWER", Icons.Default.Tv, "IR_TX_0x20DF10EF", viewModel, Modifier.weight(1f))
            IrQuickButton("AC TOGGLE", Icons.Default.AcUnit, "IR_TX_0x88001234", viewModel, Modifier.weight(1f))
            IrQuickButton("LIGHTS", Icons.Default.Lightbulb, "IR_TX_0x00FF906F", viewModel, Modifier.weight(1f))
            IrQuickButton("MUTE", Icons.Default.VolumeUp, "IR_TX_0x20DF906F", viewModel, Modifier.weight(1f))
        }
    }
}

@Composable
private fun InteractiveTouchJoystick(
    onDirectionChanged: (String, String) -> Unit
) {
    var thumbOffset by remember { mutableStateOf(Offset.Zero) }
    val maxRadius = 90.dp

    Box(
        modifier = Modifier
            .size(200.dp)
            .clip(CircleShape)
            .background(Color(0xFF040A14))
            .border(2.dp, CyberGlassBorder, CircleShape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { },
                    onDragEnd = {
                        thumbOffset = Offset.Zero
                        onDirectionChanged("STOPPED", "MOTOR_STOP")
                    },
                    onDragCancel = {
                        thumbOffset = Offset.Zero
                        onDirectionChanged("STOPPED", "MOTOR_STOP")
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val rawNew = thumbOffset + dragAmount
                        val maxPx = 70.dp.toPx()
                        val dist = hypot(rawNew.x, rawNew.y)

                        thumbOffset = if (dist > maxPx) {
                            val angle = atan2(rawNew.y, rawNew.x)
                            Offset(cos(angle) * maxPx, sin(angle) * maxPx)
                        } else {
                            rawNew
                        }

                        // Determine vector direction
                        val angleDeg = Math.toDegrees(atan2(-thumbOffset.y.toDouble(), thumbOffset.x.toDouble()))
                        val dirName = when {
                            dist < 15 -> "CENTER" to "MOTOR_STOP"
                            angleDeg in 45.0..135.0 -> "FORWARD" to "DRIVE_FORWARD"
                            angleDeg in -135.0..-45.0 -> "REVERSE" to "DRIVE_REVERSE"
                            angleDeg in -45.0..45.0 -> "TURN RIGHT" to "DRIVE_RIGHT"
                            else -> "TURN LEFT" to "DRIVE_LEFT"
                        }
                        onDirectionChanged(dirName.first, dirName.second)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)

            // Inner guide rings
            drawCircle(
                color = Color(0x2000F0FF),
                radius = size.width * 0.35f,
                style = Stroke(width = 1.dp.toPx())
            )

            // Center Cross lines
            drawLine(Color(0x1500F0FF), Offset(center.x, 0f), Offset(center.x, size.height), 1.5f)
            drawLine(Color(0x1500F0FF), Offset(0f, center.y), Offset(size.width, center.y), 1.5f)

            // Joystick Thumb knob
            val thumbCenter = center + thumbOffset
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NeonCyan, ElectricTeal, NeonBlue)
                ),
                radius = 28.dp.toPx(),
                center = thumbCenter
            )
            drawCircle(
                color = Color.White,
                radius = 8.dp.toPx(),
                center = thumbCenter
            )
        }
    }
}

@Composable
private fun IrQuickButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    cmd: String,
    viewModel: RobotViewModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF091628))
            .border(1.dp, CyberGlassBorder, RoundedCornerShape(10.dp))
            .clickable { viewModel.sendCommand(cmd) }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = label, tint = NeonCyan, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = FontFamily.Monospace)
        }
    }
}
