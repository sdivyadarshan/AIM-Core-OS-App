package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.data.model.MapData
import com.example.ui.components.GlassCard
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.ElectricTeal
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.RobotViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ExploreScreen(
    viewModel: RobotViewModel,
    modifier: Modifier = Modifier
) {
    val mapData by viewModel.mapData.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val sensorData by viewModel.sensorData.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(12.dp)
            .testTag("explore_screen")
    ) {
        // Screen Title Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Explore,
                    contentDescription = "Explore Mode",
                    tint = NeonCyan,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "EXPLORE & MAPPING MODE",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Real-Time Occupancy Grid & SLAM Pathing",
                        fontSize = 10.sp,
                        color = TextSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (settings.isMapping) NeonCyan.copy(alpha = 0.2f) else Color(0xFF0D1B2E))
                    .border(1.dp, if (settings.isMapping) NeonCyan else CyberGlassBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (settings.isMapping) "MAPPING: ACTIVE" else "STANDBY",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (settings.isMapping) NeonCyan else TextSecondary,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Progress & Statistics Metrics Bar
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Explored Area: ${mapData.exploredPercent}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { mapData.exploredPercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = ElectricTeal,
                        trackColor = Color(0xFF091220)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Est. Completion: ~${mapData.estCompletionMinutes} min",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Obstacles: ${mapData.obstacles.size} points",
                        fontSize = 9.sp,
                        color = TextSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // REAL-TIME OCCUPANCY GRID CANVAS
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF040810))
                .border(1.dp, CyberGlassBorder, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            OccupancyGridCanvas(mapData = mapData, tofDistance = sensorData.tof)

            // Canvas Legend HUD Overlay
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .border(1.dp, CyberGlassBorder, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(NeonCyan))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AIM-Bot Position", fontSize = 9.sp, color = TextPrimary, fontFamily = FontFamily.Monospace)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusRed))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Detected Obstacle", fontSize = 9.sp, color = TextPrimary, fontFamily = FontFamily.Monospace)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ElectricTeal))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Path Trail", fontSize = 9.sp, color = TextPrimary, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Mapping Action Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { viewModel.toggleMapping() },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("toggle_mapping_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (settings.isMapping) StatusRed else StatusGreen,
                    contentColor = CyberDarkBg
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (settings.isMapping) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (settings.isMapping) "PAUSE MAPPING" else "START EXPLORE MAPPING",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Button(
                onClick = { viewModel.runDiagnostics() },
                modifier = Modifier
                    .height(48.dp)
                    .testTag("refresh_lidar_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricTeal,
                    contentColor = CyberDarkBg
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh SLAM", modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun OccupancyGridCanvas(
    mapData: MapData,
    tofDistance: Int
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 1. Cyber Grid Lines
        val step = 30.dp.toPx()
        for (x in 0..w.toInt() step step.toInt()) {
            drawLine(
                color = Color(0x1500F0FF),
                start = Offset(x.toFloat(), 0f),
                end = Offset(x.toFloat(), h),
                strokeWidth = 1f
            )
        }
        for (y in 0..h.toInt() step step.toInt()) {
            drawLine(
                color = Color(0x1500F0FF),
                start = Offset(0f, y.toFloat()),
                end = Offset(w, y.toFloat()),
                strokeWidth = 1f
            )
        }

        // 2. Explored Area Polygon / Radial Shading
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x2000F0FF),
                    Color(0x050072FF),
                    Color.Transparent
                ),
                center = Offset(w * mapData.robotX, h * mapData.robotY),
                radius = w * 0.45f
            ),
            radius = w * 0.45f,
            center = Offset(w * mapData.robotX, h * mapData.robotY)
        )

        // 3. SLAM Trajectory Path Trail
        if (mapData.pathTrail.size > 1) {
            val path = Path()
            val first = mapData.pathTrail.first()
            path.moveTo(w * first.first, h * first.second)
            for (pt in mapData.pathTrail.drop(1)) {
                path.lineTo(w * pt.first, h * pt.second)
            }
            drawPath(
                path = path,
                color = ElectricTeal,
                style = Stroke(width = 3.dp.toPx())
            )
        }

        // 4. Detected Obstacle Grid Points
        mapData.obstacles.forEach { obstacle ->
            val obsX = w * obstacle.x
            val obsY = h * obstacle.y
            drawCircle(
                color = StatusRed.copy(alpha = 0.85f),
                radius = 7.dp.toPx(),
                center = Offset(obsX, obsY)
            )
            drawCircle(
                color = StatusRed,
                radius = 3.dp.toPx(),
                center = Offset(obsX, obsY)
            )
        }

        // 5. Robot Position Marker with Directional Heading Angle Triangle
        val rx = w * mapData.robotX
        val ry = h * mapData.robotY

        // ToF Scan Cone
        val coneLength = (tofDistance.coerceIn(10, 150) * 1.5f).dp.toPx()
        val rad = Math.toRadians(mapData.headingAngle.toDouble())
        val conePath = Path().apply {
            moveTo(rx, ry)
            lineTo(
                rx + (coneLength * cos(rad - 0.3)).toFloat(),
                ry + (coneLength * sin(rad - 0.3)).toFloat()
            )
            lineTo(
                rx + (coneLength * cos(rad + 0.3)).toFloat(),
                ry + (coneLength * sin(rad + 0.3)).toFloat()
            )
            close()
        }
        drawPath(
            path = conePath,
            color = NeonCyan.copy(alpha = 0.25f)
        )

        // Robot Direction Arrow
        val arrowLength = 16.dp.toPx()
        val arrowPath = Path().apply {
            moveTo(
                rx + (arrowLength * cos(rad)).toFloat(),
                ry + (arrowLength * sin(rad)).toFloat()
            )
            lineTo(
                rx + (arrowLength * 0.6f * cos(rad + 2.4)).toFloat(),
                ry + (arrowLength * 0.6f * sin(rad + 2.4)).toFloat()
            )
            lineTo(rx, ry)
            lineTo(
                rx + (arrowLength * 0.6f * cos(rad - 2.4)).toFloat(),
                ry + (arrowLength * 0.6f * sin(rad - 2.4)).toFloat()
            )
            close()
        }
        drawPath(path = arrowPath, color = NeonCyan)
        drawCircle(color = Color.White, radius = 4.dp.toPx(), center = Offset(rx, ry))
    }
}
