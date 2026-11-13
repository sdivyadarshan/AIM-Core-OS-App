package com.example.ui.components

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
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RobotEyeState
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun RobotEyesView(
    eyeState: RobotEyeState,
    modifier: Modifier = Modifier,
    onEyesClick: () -> Unit = {}
) {
    val targetColor = eyeState.color
    val animatedEyeColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 600),
        label = "EyeColorAnimation"
    )

    // Blink effect logic: periodic eye closure
    var isBlinking by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3500) // Blink every 3.5 seconds
            isBlinking = true
            delay(150)
            isBlinking = false
        }
    }

    val eyeHeightScale by animateFloatAsState(
        targetValue = if (isBlinking) 0.08f else 1.0f,
        animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing),
        label = "BlinkScale"
    )

    // Pulse transition for cyber eye scanning ring
    val infiniteTransition = rememberInfiniteTransition(label = "EyePulse")
    val scanLineY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ScanLine"
    )

    val eyeGlowRadius by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "EyeGlow"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, CyberGlassBorder, RoundedCornerShape(20.dp))
            .clickable { onEyesClick() }
            .testTag("robot_eyes_view"),
        color = CyberSurface.copy(alpha = 0.75f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Visor Housing Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Robot Visor",
                        tint = animatedEyeColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AIM-BOT VISOR v1.0",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(animatedEyeColor.copy(alpha = 0.15f))
                        .border(1.dp, animatedEyeColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = eyeState.label,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = animatedEyeColor,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Visor Eye Socket Screen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF060911),
                                Color(0xFF0C1322)
                            )
                        )
                    )
                    .border(1.dp, animatedEyeColor.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Background Grid lines inside visor
                Canvas(modifier = Modifier.matchParentSize()) {
                    val gridSpacing = 20.dp.toPx()
                    for (x in 0..size.width.toInt() step gridSpacing.toInt()) {
                        drawLine(
                            color = Color(0x1100F0FF),
                            start = Offset(x.toFloat(), 0f),
                            end = Offset(x.toFloat(), size.height),
                            strokeWidth = 1f
                        )
                    }
                    val scanY = scanLineY * size.height
                    drawLine(
                        color = animatedEyeColor.copy(alpha = 0.25f),
                        start = Offset(0f, scanY),
                        end = Offset(size.width, scanY),
                        strokeWidth = 2f
                    )
                }

                // Dual Robot Eyes (Left Eye & Right Eye)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SingleRobotEye(
                        color = animatedEyeColor,
                        heightScale = eyeHeightScale,
                        glowScale = eyeGlowRadius
                    )

                    SingleRobotEye(
                        color = animatedEyeColor,
                        heightScale = eyeHeightScale,
                        glowScale = eyeGlowRadius
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Sensors,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "HANDD Innovs Autonomous Nav System",
                    fontSize = 10.sp,
                    color = TextSecondary,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
private fun SingleRobotEye(
    color: Color,
    heightScale: Float,
    glowScale: Float
) {
    Box(
        modifier = Modifier.size(54.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer Glowing Aura
        Canvas(modifier = Modifier.matchParentSize()) {
            val eyeWidth = size.width * 0.75f
            val eyeHeight = size.height * 0.6f * heightScale

            drawRoundRect(
                color = color.copy(alpha = 0.2f),
                topLeft = Offset((size.width - eyeWidth * glowScale) / 2f, (size.height - eyeHeight * glowScale) / 2f),
                size = Size(eyeWidth * glowScale, eyeHeight * glowScale),
                cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx())
            )

            // Inner Eye Core
            drawRoundRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        color,
                        color.copy(alpha = 0.8f)
                    )
                ),
                topLeft = Offset((size.width - eyeWidth) / 2f, (size.height - eyeHeight) / 2f),
                size = Size(eyeWidth, eyeHeight),
                cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
            )

            // Pupil Aperture highlight line
            if (heightScale > 0.3f) {
                drawRoundRect(
                    color = color.copy(alpha = 0.6f),
                    topLeft = Offset((size.width - eyeWidth * 0.4f) / 2f, (size.height - eyeHeight * 0.4f) / 2f),
                    size = Size(eyeWidth * 0.4f, eyeHeight * 0.4f),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}
