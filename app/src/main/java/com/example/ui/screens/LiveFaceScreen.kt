package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RobotEyeState
import com.example.ui.components.RightMiddleDialerMenu
import com.example.ui.components.RobotTab
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
import kotlinx.coroutines.delay

@Composable
fun LiveFaceScreen(
    viewModel: RobotViewModel,
    onTabSelected: (RobotTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val eyeState by viewModel.eyeState.collectAsState()
    val sensorData by viewModel.sensorData.collectAsState()

    var isDialerOpen by remember { mutableStateOf(false) }
    var tapFeedbackText by remember { mutableStateOf<String?>(null) }

    // Triple tap state tracking
    var tapCount by remember { mutableIntStateOf(0) }
    var lastTapTime by remember { mutableLongStateOf(0L) }

    // Tap feedback auto-clear
    LaunchedEffect(tapFeedbackText) {
        if (tapFeedbackText != null) {
            delay(1800)
            tapFeedbackText = null
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastTapTime < 500) {
                            tapCount++
                        } else {
                            tapCount = 1
                        }
                        lastTapTime = currentTime

                        if (tapCount >= 3) {
                            // TRIPLE TAP DETECTED!
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            tapCount = 0
                            isDialerOpen = true
                            tapFeedbackText = "TRIPLE TAP DETECTED! Opening Options Dialer..."
                        } else {
                            // Single tap feedback
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.setEyeState(RobotEyeState.HAPPY)
                            tapFeedbackText = "Tap (${tapCount}/3) - Triple Tap Eyes for Menu"
                        }
                    }
                )
            }
            .testTag("live_face_screen")
    ) {
        // Cyber Grid Animated Background
        CyberGridBackground()

        // 1. TOP LEFT: CURRENT MODE INDICATION
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF071426).copy(alpha = 0.9f))
                .border(1.dp, eyeState.color.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .testTag("current_mode_badge")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(eyeState.color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "MODE: ${eyeState.label}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = eyeState.color,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "TRIPLE TAP EYES FOR OPTIONS DIALER",
                        fontSize = 7.5.sp,
                        color = TextSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // 2. TOP RIGHT: BATTERY INDICATION
        val batteryColor = when {
            sensorData.battery > 50 -> StatusGreen
            sensorData.battery > 20 -> StatusYellow
            else -> StatusRed
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 16.dp, top = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF071426).copy(alpha = 0.9f))
                .border(1.dp, batteryColor.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .testTag("battery_indicator_badge")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.BatteryChargingFull,
                    contentDescription = "Battery",
                    tint = batteryColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${sensorData.battery}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = batteryColor,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // 3. CENTER: EXPRESSIVE ANIMATED EYES
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedRoboticEyes(
                eyeState = eyeState,
                tofDistance = sensorData.tof
            )
        }

        // 4. RIGHT-MIDDLE DIALER TRIGGER HANDLE
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 4.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                .background(NeonCyan.copy(alpha = 0.25f))
                .border(1.dp, NeonCyan, RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                .clickable { isDialerOpen = true }
                .padding(horizontal = 8.dp, vertical = 18.dp)
                .testTag("dialer_trigger_handle")
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open Dialer",
                    tint = NeonCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "DIAL",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Gesture Feedback Toast Banner
        tapFeedbackText?.let { text ->
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF091C30).copy(alpha = 0.95f))
                    .border(1.dp, NeonCyan, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = text,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // 5. RIGHT MIDDLE ROTARY DIALER OVERLAY
        RightMiddleDialerMenu(
            isOpen = isDialerOpen,
            currentTab = RobotTab.HOME,
            onTabSelected = { selectedTab ->
                onTabSelected(selectedTab)
                isDialerOpen = false
            },
            onClose = { isDialerOpen = false }
        )
    }
}

@Composable
fun AnimatedRoboticEyes(
    eyeState: RobotEyeState,
    tofDistance: Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "eyeAnimations")

    // Cute Gentle breathing bounce (EMO pet style)
    val breathingY by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathingY"
    )

    // Pupil Horizontal Sway
    val pupilOffsetX by infiniteTransition.animateFloat(
        initialValue = -22f,
        targetValue = 22f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pupilOffset"
    )

    // Scanning Arc Rotation
    val scanRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanRotation"
    )

    val targetColor by animateColorAsState(targetValue = eyeState.color, label = "eyeColor")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.graphicsLayer { translationY = breathingY }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Cute Kawaii Eye
            SingleRoboticEye(
                isLeft = true,
                eyeColor = targetColor,
                eyeState = eyeState,
                pupilOffsetX = pupilOffsetX,
                scanRotation = scanRotation,
                isBlinking = (System.currentTimeMillis() % 3800) < 220
            )

            // Right Cute Kawaii Eye
            SingleRoboticEye(
                isLeft = false,
                eyeColor = targetColor,
                eyeState = eyeState,
                pupilOffsetX = pupilOffsetX,
                scanRotation = scanRotation,
                isBlinking = (System.currentTimeMillis() % 3800) < 220
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Kawaii Pet Expressive Mouth & Blushing Cheeks
        KawaiiMouth(
            eyeState = eyeState,
            accentColor = targetColor
        )
    }
}

@Composable
private fun SingleRoboticEye(
    isLeft: Boolean,
    eyeColor: Color,
    eyeState: RobotEyeState,
    pupilOffsetX: Float,
    scanRotation: Float,
    isBlinking: Boolean
) {
    val eyeScaleY by animateFloatAsState(
        targetValue = when {
            isBlinking -> 0.05f
            eyeState == RobotEyeState.HAPPY -> 0.65f
            eyeState == RobotEyeState.WARNING || eyeState == RobotEyeState.LOW_BATTERY -> 0.5f
            eyeState == RobotEyeState.OBSTACLE_DETECTED -> 1.15f
            else -> 1.0f
        },
        animationSpec = tween(durationMillis = 180),
        label = "eyeScaleY"
    )

    val eyeRotationTilt by animateFloatAsState(
        targetValue = when (eyeState) {
            RobotEyeState.HAPPY -> if (isLeft) -8f else 8f
            RobotEyeState.EXPLORING -> if (isLeft) 6f else -6f
            RobotEyeState.WARNING, RobotEyeState.LOW_BATTERY -> if (isLeft) 10f else -10f
            else -> 0f
        },
        animationSpec = tween(durationMillis = 220),
        label = "eyeRotationTilt"
    )

    // Big Kawaii Eye Socket
    Box(
        modifier = Modifier
            .size(width = 175.dp, height = 185.dp)
            .graphicsLayer {
                rotationZ = eyeRotationTilt
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cornerRadius = 54.dp.toPx()

            val eyeHeight = h * eyeScaleY
            val eyeTop = (h - eyeHeight) / 2f

            // 0. Cute Eyebrows above Eye Socket
            val browPath = androidx.compose.ui.graphics.Path().apply {
                when (eyeState) {
                    RobotEyeState.HAPPY, RobotEyeState.IDLE -> {
                        moveTo(w * 0.2f, eyeTop - 12f)
                        quadraticTo(w * 0.5f, eyeTop - 24f, w * 0.8f, eyeTop - 12f)
                    }
                    RobotEyeState.WARNING, RobotEyeState.LOW_BATTERY -> {
                        // Worried / sad eyebrows (\ /)
                        if (isLeft) {
                            moveTo(w * 0.15f, eyeTop - 22f)
                            lineTo(w * 0.82f, eyeTop - 10f)
                        } else {
                            moveTo(w * 0.18f, eyeTop - 10f)
                            lineTo(w * 0.85f, eyeTop - 22f)
                        }
                    }
                    RobotEyeState.OBSTACLE_DETECTED -> {
                        // High shocked eyebrows
                        moveTo(w * 0.2f, eyeTop - 28f)
                        quadraticTo(w * 0.5f, eyeTop - 36f, w * 0.8f, eyeTop - 28f)
                    }
                    else -> {
                        moveTo(w * 0.25f, eyeTop - 14f)
                        lineTo(w * 0.75f, eyeTop - 14f)
                    }
                }
            }
            drawPath(
                path = browPath,
                color = eyeColor,
                style = Stroke(width = 4.5.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
            )

            // 1. Soft Kawaii Aura Glow
            drawRoundRect(
                color = eyeColor.copy(alpha = 0.22f),
                topLeft = Offset(-6f, eyeTop - 6f),
                size = Size(w + 12f, eyeHeight + 12f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius + 6f)
            )

            // Outer Bright Cyber Border Ring
            drawRoundRect(
                color = eyeColor.copy(alpha = 0.85f),
                topLeft = Offset(0f, eyeTop),
                size = Size(w, eyeHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius),
                style = Stroke(width = 5.dp.toPx())
            )

            // 2. Main Kawaii Eye Fill
            if (eyeState == RobotEyeState.HAPPY && !isBlinking) {
                // Cute Arc Happy Eyes (^ ^)
                val strokeWidth = 18.dp.toPx()
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.16f, eyeTop + eyeHeight * 0.65f)
                    quadraticTo(
                        w * 0.5f, eyeTop + eyeHeight * 0.08f,
                        w * 0.84f, eyeTop + eyeHeight * 0.65f
                    )
                }
                drawPath(
                    path = path,
                    color = eyeColor,
                    style = Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                )
            } else {
                // Glossy Kawaii Fill (Gradient)
                drawRoundRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            eyeColor.copy(alpha = 0.98f),
                            eyeColor.copy(alpha = 0.75f),
                            eyeColor.copy(alpha = 0.4f)
                        ),
                        center = Offset(w * 0.45f, eyeTop + eyeHeight * 0.38f),
                        radius = w * 0.8f
                    ),
                    topLeft = Offset(3f, eyeTop + 3f),
                    size = Size(w - 6f, eyeHeight - 6f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius - 2f)
                )

                // 3. Pupil & Sparkly Catchlights (Anime/Kawaii Sparkles)
                if (!isBlinking) {
                    val currPupilX = if (eyeState == RobotEyeState.SEARCHING || eyeState == RobotEyeState.EXPLORING) {
                        (w / 2f) + pupilOffsetX
                    } else {
                        w / 2f
                    }
                    val currPupilY = eyeTop + (eyeHeight / 2f)

                    // Pupil Core Lens
                    val pupilRadius = 24.dp.toPx()
                    drawCircle(
                        color = Color.White,
                        radius = pupilRadius,
                        center = Offset(currPupilX, currPupilY)
                    )

                    drawCircle(
                        color = eyeColor,
                        radius = pupilRadius * 0.55f,
                        center = Offset(currPupilX, currPupilY)
                    )

                    // TRIPLE KAWAII CATCHLIGHT SPARKLES (Matching image!)
                    // 1. Major Sparkle (Upper Left)
                    drawCircle(
                        color = Color.White,
                        radius = 16.dp.toPx(),
                        center = Offset(w * 0.32f, eyeTop + eyeHeight * 0.28f)
                    )

                    // 2. Secondary Sparkle (Lower Right)
                    drawCircle(
                        color = Color.White.copy(alpha = 0.9f),
                        radius = 9.dp.toPx(),
                        center = Offset(w * 0.68f, eyeTop + eyeHeight * 0.7f)
                    )

                    // 3. Mini Sparkle Dot (Top Right)
                    drawCircle(
                        color = Color.White,
                        radius = 5.dp.toPx(),
                        center = Offset(w * 0.66f, eyeTop + eyeHeight * 0.24f)
                    )
                }
            }

            // 4. Rosy Pink Blushing Cheeks (directly below eye)
            val blushY = eyeTop + eyeHeight + 14.dp.toPx()
            drawOval(
                color = Color(0xFFFF69B4).copy(alpha = 0.45f),
                topLeft = Offset(w * 0.2f, blushY),
                size = Size(w * 0.6f, 18.dp.toPx())
            )

            // 5. Cute Eyelashes on top outer corner
            val lashX = if (isLeft) w * 0.1f else w * 0.9f
            val lashDir = if (isLeft) -1f else 1f
            drawLine(
                color = eyeColor,
                start = Offset(lashX, eyeTop + 8f),
                end = Offset(lashX + lashDir * 14f, eyeTop - 10f),
                strokeWidth = 3.5.dp.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            drawLine(
                color = eyeColor,
                start = Offset(lashX + lashDir * 12f, eyeTop + 18f),
                end = Offset(lashX + lashDir * 24f, eyeTop + 6f),
                strokeWidth = 3.5.dp.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        }
    }
}

@Composable
private fun KawaiiMouth(
    eyeState: RobotEyeState,
    accentColor: Color
) {
    Canvas(
        modifier = Modifier.size(width = 110.dp, height = 48.dp)
    ) {
        val w = size.width
        val h = size.height

        when (eyeState) {
            RobotEyeState.HAPPY, RobotEyeState.EXPLORING -> {
                // Cute Open Smiling Mouth with Tongue (Top Row of Image)
                val mouthPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.2f, h * 0.25f)
                    quadraticTo(w * 0.5f, h * 0.95f, w * 0.8f, h * 0.25f)
                    close()
                }
                drawPath(path = mouthPath, color = Color(0xFFFF4081))

                // Tongue inside
                val tonguePath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.35f, h * 0.6f)
                    quadraticTo(w * 0.5f, h * 0.92f, w * 0.65f, h * 0.6f)
                    close()
                }
                drawPath(path = tonguePath, color = Color(0xFFFF80AB))
            }

            RobotEyeState.OBSTACLE_DETECTED -> {
                // Shocked Open Round Mouth 'o' (3rd row 1st column)
                drawOval(
                    color = Color(0xFFFF4081),
                    topLeft = Offset(w * 0.35f, h * 0.15f),
                    size = Size(w * 0.3f, h * 0.7f)
                )
            }

            RobotEyeState.WARNING, RobotEyeState.LOW_BATTERY -> {
                // Sad pouting mouth curve (Row 4 Column 3 / Row 2 Column 1)
                val poutingPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.25f, h * 0.65f)
                    quadraticTo(w * 0.5f, h * 0.15f, w * 0.75f, h * 0.65f)
                }
                drawPath(
                    path = poutingPath,
                    color = Color(0xFFFF5252),
                    style = Stroke(width = 6.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                )
            }

            else -> {
                // Cute Cat Mouth ':3' / 'u' (Row 1 Column 3 / Row 2 Column 2)
                val leftCurve = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.2f, h * 0.35f)
                    quadraticTo(w * 0.35f, h * 0.75f, w * 0.5f, h * 0.35f)
                }
                val rightCurve = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.5f, h * 0.35f)
                    quadraticTo(w * 0.65f, h * 0.75f, w * 0.8f, h * 0.35f)
                }
                drawPath(
                    path = leftCurve,
                    color = accentColor,
                    style = Stroke(width = 5.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                )
                drawPath(
                    path = rightCurve,
                    color = accentColor,
                    style = Stroke(width = 5.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                )
            }
        }
    }
}

@Composable
private fun CyberGridBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val gridSpacing = 44.dp.toPx()
        val gridColor = Color(0x0E00F0FF)

        for (x in 0..size.width.toInt() step gridSpacing.toInt()) {
            drawLine(
                color = gridColor,
                start = Offset(x.toFloat(), 0f),
                end = Offset(x.toFloat(), size.height),
                strokeWidth = 1f
            )
        }

        for (y in 0..size.height.toInt() step gridSpacing.toInt()) {
            drawLine(
                color = gridColor,
                start = Offset(0f, y.toFloat()),
                end = Offset(size.width, y.toFloat()),
                strokeWidth = 1f
            )
        }
    }
}
