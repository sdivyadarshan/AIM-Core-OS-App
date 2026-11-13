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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RobotEyeState
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

data class EyeStateMeaning(
    val state: RobotEyeState,
    val title: String,
    val shortCode: String,
    val description: String,
    val behaviorTrigger: String
)

val AIMBOT_EYE_GUIDE = listOf(
    EyeStateMeaning(
        state = RobotEyeState.IDLE,
        title = "System Idle Mode",
        shortCode = "IDLE",
        description = "Baseline operational state with natural gentle eye blinking and resting pupil position.",
        behaviorTrigger = "Default state when robot is waiting for commands or resting in place."
    ),
    EyeStateMeaning(
        state = RobotEyeState.SCANNING,
        title = "Scanning Environment",
        shortCode = "SCAN",
        description = "Active cyber arc rotating around iris while measuring ToF distance and IR sensors.",
        behaviorTrigger = "Triggered when mapping room boundaries, scanning obstacles, or sensing light."
    ),
    EyeStateMeaning(
        state = RobotEyeState.THINKING,
        title = "AI Neural Processing",
        shortCode = "THINK",
        description = "Fast spinning teal arc matrix indicating background LLM inference and path planning.",
        behaviorTrigger = "Triggered while executing Gemini AI voice queries, obstacle path calculation, or vision processing."
    ),
    EyeStateMeaning(
        state = RobotEyeState.SEARCHING,
        title = "Target Searching",
        shortCode = "SRCH",
        description = "Horizontal side-to-side pupil tracking searching for objects, faces, or Bluetooth beacons.",
        behaviorTrigger = "Active when seeking target room objects, following human gestures, or object tracking."
    ),
    EyeStateMeaning(
        state = RobotEyeState.HAPPY,
        title = "Nominal / Happy",
        shortCode = "HAPPY",
        description = "Cheerful curved eye aperture glowing bright green indicating task success or user touch.",
        behaviorTrigger = "Triggered when user taps screen face, completes navigation route, or receives compliment."
    ),
    EyeStateMeaning(
        state = RobotEyeState.WARNING,
        title = "Caution Warning",
        shortCode = "WARN",
        description = "Narrowed amber yellow eyes warning of near proximity or high CPU load.",
        behaviorTrigger = "Triggered when ToF sensor detects object < 30cm or motor driver temperature rises."
    ),
    EyeStateMeaning(
        state = RobotEyeState.LOW_BATTERY,
        title = "Low Battery Alert",
        shortCode = "BATTERY",
        description = "Flashing crimson red pupils indicating battery level has dropped below 15%.",
        behaviorTrigger = "Triggered automatically when LiPo battery drops below 15% threshold."
    ),
    EyeStateMeaning(
        state = RobotEyeState.DISCONNECTED,
        title = "ESP32 Offline",
        shortCode = "OFFLINE",
        description = "Dimmed gray offline aperture indicating lost socket connection with ESP32 hardware.",
        behaviorTrigger = "Active when Wi-Fi or websocket connection to ESP32 board is lost."
    ),
    EyeStateMeaning(
        state = RobotEyeState.OBSTACLE_DETECTED,
        title = "Obstacle Blocked",
        shortCode = "OBSTACLE",
        description = "Focused yellow/red iris locking on immediate wall or physical obstacle ahead.",
        behaviorTrigger = "Triggered during drive mode when ToF distance drops below 20cm hazard boundary."
    ),
    EyeStateMeaning(
        state = RobotEyeState.CHARGING,
        title = "Charging Power",
        shortCode = "CHARGE",
        description = "Pulsing green energy aura around iris indicating power cable plugged in.",
        behaviorTrigger = "Triggered when robot is docked on charging pad or USB-C power connected."
    ),
    EyeStateMeaning(
        state = RobotEyeState.NAVIGATING,
        title = "Autonomous Nav",
        shortCode = "NAV",
        description = "Vector-aligned eye orientation indicating active motion along planned waypoint trajectory.",
        behaviorTrigger = "Active during indoor route navigation to target destination."
    ),
    EyeStateMeaning(
        state = RobotEyeState.EXPLORING,
        title = "Room Exploring",
        shortCode = "EXPLORE",
        description = "Wide curious blue aperture scanning 360-degree surroundings to build occupancy grid.",
        behaviorTrigger = "Active during indoor SLAM mapping and room exploration mode."
    ),
    EyeStateMeaning(
        state = RobotEyeState.RC_MODE,
        title = "Manual RC Drive",
        shortCode = "RC",
        description = "Focused high-contrast cyan aperture optimized for manual joystick remote control.",
        behaviorTrigger = "Active when user is steering robot via RC Drive joystick screen."
    ),
    EyeStateMeaning(
        state = RobotEyeState.ASSISTANT_MODE,
        title = "Room AI Assistant",
        shortCode = "ASSIST",
        description = "Gentle glowing teal aura actively listening to room commands and controlling smart devices.",
        behaviorTrigger = "Active during Room Assistant interaction and smart device automation."
    )
)

@Composable
fun UnderstandingAimbotScreen(
    viewModel: RobotViewModel,
    onBackToHome: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val currentEyeState by viewModel.eyeState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(12.dp)
            .testTag("understanding_aimbot_screen")
    ) {
        // Top Bar Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBackToHome != null) {
                IconButton(
                    onClick = onBackToHome,
                    modifier = Modifier.testTag("aimbot_guide_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = NeonCyan
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = "Understanding AIM-Bot",
                    tint = NeonCyan,
                    modifier = Modifier
                        .size(28.dp)
                        .padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "UNDERSTANDING AIM-BOT",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Guide to Expressive Eye Movements, Status Signals & Robotic Behaviors",
                    fontSize = 10.sp,
                    color = NeonCyan,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Current Active Eye Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(currentEyeState.color.copy(alpha = 0.2f))
                    .border(1.dp, currentEyeState.color, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(currentEyeState.color)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ACTIVE: ${currentEyeState.name}",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = currentEyeState.color,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Grid of Eye State Meanings
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(AIMBOT_EYE_GUIDE) { guide ->
                val isActive = guide.state == currentEyeState

                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setEyeState(guide.state) },
                    borderWidth = if (isActive) 2.dp else 1.dp,
                    borderColor = if (isActive) guide.state.color else CyberGlassBorder,
                    cornerRadius = 14.dp
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(guide.state.color)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = guide.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isActive) guide.state.color else TextPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            if (isActive) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Active",
                                    tint = guide.state.color,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Text(
                            text = guide.description,
                            fontSize = 9.5.sp,
                            color = TextSecondary,
                            lineHeight = 13.sp
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF091628))
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "TRIGGER: ${guide.behaviorTrigger}",
                                fontSize = 8.5.sp,
                                color = guide.state.color,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Test button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isActive) guide.state.color.copy(alpha = 0.3f) else Color(0xFF0D2137))
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isActive) "TESTING EXPRESSION LIVE" else "TAP TO PREVIEW EXPRESSION",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isActive) guide.state.color else TextSecondary,
                                fontFamily = FontFamily.Monospace
                            )
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = if (isActive) guide.state.color else TextSecondary,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
