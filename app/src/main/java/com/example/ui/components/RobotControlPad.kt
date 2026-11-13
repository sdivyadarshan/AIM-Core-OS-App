package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun RobotControlPad(
    isMapping: Boolean,
    onCommand: (String) -> Unit,
    onToggleMapping: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.testTag("robot_control_pad")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MANUAL OVERRIDE & DRIVE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = 1.2.sp,
                    fontFamily = FontFamily.Monospace
                )

                // Mapping Toggle Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isMapping) StatusGreen.copy(alpha = 0.2f) else CyberGlassBorder)
                        .border(
                            1.dp,
                            if (isMapping) StatusGreen else TextSecondary,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onToggleMapping() }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "Autonomous Mapping",
                            tint = if (isMapping) StatusGreen else TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isMapping) "MAPPING ACTIVE" else "START MAPPING",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isMapping) StatusGreen else TextSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // D-PAD & Emergency Stop Controls
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Forward
                DirectionButton(
                    icon = Icons.Default.ArrowUpward,
                    label = "FWD",
                    onClick = { onCommand("MOVE_FORWARD") },
                    testTag = "dpad_forward"
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left
                    DirectionButton(
                        icon = Icons.Default.ArrowBack,
                        label = "LEFT",
                        onClick = { onCommand("TURN_LEFT") },
                        testTag = "dpad_left"
                    )

                    // EMERGENCY STOP BUTTON
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(StatusRed)
                            .border(2.dp, Color.White, CircleShape)
                            .clickable { onCommand("EMERGENCY_STOP") }
                            .testTag("emergency_stop_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "STOP",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "STOP",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Right
                    DirectionButton(
                        icon = Icons.Default.ArrowForward,
                        label = "RIGHT",
                        onClick = { onCommand("TURN_RIGHT") },
                        testTag = "dpad_right"
                    )
                }

                // Reverse
                DirectionButton(
                    icon = Icons.Default.ArrowDownward,
                    label = "REV",
                    onClick = { onCommand("MOVE_REVERSE") },
                    testTag = "dpad_reverse"
                )
            }
        }
    }
}

@Composable
private fun DirectionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F1A2E))
            .border(1.dp, CyberGlassBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = NeonCyan,
            modifier = Modifier.size(22.dp)
        )
    }
}
