package com.example.ui.screens

import android.Manifest
import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.data.model.RobotSnapshot
import com.example.ui.components.GlassCard
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.ElectricTeal
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.RobotViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    viewModel: RobotViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val sensorData by viewModel.sensorData.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val snapshots by viewModel.snapshots.collectAsState()

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var isCameraActive by remember { mutableStateOf(false) }
    var flashFeedback by remember { mutableStateOf(false) }

    LaunchedEffect(cameraPermissionState.status.isGranted) {
        if (cameraPermissionState.status.isGranted) {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                try {
                    cameraProvider = cameraProviderFuture.get()
                    isCameraActive = true
                } catch (e: Exception) {
                    isCameraActive = false
                }
            }, ContextCompat.getMainExecutor(context))
        } else {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .testTag("camera_screen")
    ) {
        // Live Camera Preview View or Fallback Cyber Viewfinder
        if (cameraPermissionState.status.isGranted && isCameraActive) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val preview = Preview.Builder().build()
                    val selector = CameraSelector.DEFAULT_BACK_CAMERA
                    preview.setSurfaceProvider(previewView.surfaceProvider)
                    try {
                        cameraProvider?.unbindAll()
                        cameraProvider?.bindToLifecycle(lifecycleOwner, selector, preview)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // High-fidelity fallback simulated camera viewfinder canvas
            SimulatedCameraViewfinder(
                tofDistance = sensorData.tof,
                aiDetectionEnabled = settings.aiDetectionEnabled
            )
        }

        // Camera HUD Overlays & Bounding Box for Future Object Detection
        CameraHudOverlay(
            tofDistance = sensorData.tof,
            aiEnabled = settings.aiDetectionEnabled,
            onToggleAi = { viewModel.toggleAiDetection() }
        )

        // Shutter Feedback Flash Overlay
        if (flashFeedback) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.6f))
            )
            LaunchedEffect(flashFeedback) {
                kotlinx.coroutines.delay(100)
                flashFeedback = false
            }
        }

        // Bottom Camera Controls & Captured Snapshots Row
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Captured Snapshots Gallery Row
            if (snapshots.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(snapshots) { snap ->
                        SnapshotThumbnail(snapshot = snap)
                    }
                }
            }

            // Capture Controls Panel
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 24.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // AI Toggle Button
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (settings.aiDetectionEnabled) NeonCyan.copy(alpha = 0.2f) else CyberDarkBg)
                            .border(1.dp, if (settings.aiDetectionEnabled) NeonCyan else TextSecondary, CircleShape)
                            .clickable { viewModel.toggleAiDetection() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Object Detection",
                            tint = if (settings.aiDetectionEnabled) NeonCyan else TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // CAPTURE IMAGE BUTTON
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(NeonCyan, ElectricTeal)
                                )
                            )
                            .border(3.dp, Color.White, CircleShape)
                            .clickable {
                                flashFeedback = true
                                viewModel.captureSnapshot(
                                    tof = sensorData.tof,
                                    label = if (settings.aiDetectionEnabled) "AI Object Obstacle" else "Camera Image"
                                )
                            }
                            .testTag("capture_image_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Capture Image",
                            tint = CyberDarkBg,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    // Radar ToF Sync Button
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(CyberDarkBg)
                            .border(1.dp, CyberGlassBorder, CircleShape)
                            .clickable { viewModel.runDiagnostics() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Radar,
                            contentDescription = "Radar HUD Sync",
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SimulatedCameraViewfinder(
    tofDistance: Int,
    aiDetectionEnabled: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF040914),
                        Color(0xFF081226)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridSpacing = 40.dp.toPx()
            for (x in 0..size.width.toInt() step gridSpacing.toInt()) {
                drawLine(
                    color = Color(0x1500F0FF),
                    start = Offset(x.toFloat(), 0f),
                    end = Offset(x.toFloat(), size.height),
                    strokeWidth = 1f
                )
            }
            for (y in 0..size.height.toInt() step gridSpacing.toInt()) {
                drawLine(
                    color = Color(0x1500F0FF),
                    start = Offset(0f, y.toFloat()),
                    end = Offset(size.width, y.toFloat()),
                    strokeWidth = 1f
                )
            }
        }
    }
}

@Composable
private fun CameraHudOverlay(
    tofDistance: Int,
    aiEnabled: Boolean,
    onToggleAi: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Top HUD bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .border(1.dp, CyberGlassBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Camera,
                        contentDescription = null,
                        tint = StatusGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CAM0: 1080P @ 30FPS | ToF: ${tofDistance}cm",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (aiEnabled) NeonCyan.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.6f))
                    .border(1.dp, if (aiEnabled) NeonCyan else CyberGlassBorder, RoundedCornerShape(8.dp))
                    .clickable { onToggleAi() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (aiEnabled) "AI DETECT: ON" else "AI DETECT: OFF",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (aiEnabled) NeonCyan else TextSecondary,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Center HUD Crosshair & Object Bounding Box Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 120.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(220.dp)) {
                val center = Offset(size.width / 2f, size.height / 2f)

                // Outer Crosshair Ring
                drawCircle(
                    color = NeonCyan.copy(alpha = 0.4f),
                    radius = size.width / 2f,
                    style = Stroke(width = 1.5.dp.toPx())
                )

                // Corner brackets for bounding box
                val bracketLen = 20.dp.toPx()
                val color = if (tofDistance < 30) StatusYellow else NeonCyan
                // Top-Left
                drawLine(color, Offset(0f, 0f), Offset(bracketLen, 0f), 3f)
                drawLine(color, Offset(0f, 0f), Offset(0f, bracketLen), 3f)
                // Top-Right
                drawLine(color, Offset(size.width, 0f), Offset(size.width - bracketLen, 0f), 3f)
                drawLine(color, Offset(size.width, 0f), Offset(size.width, bracketLen), 3f)
                // Bottom-Left
                drawLine(color, Offset(0f, size.height), Offset(bracketLen, size.height), 3f)
                drawLine(color, Offset(0f, size.height), Offset(0f, size.height - bracketLen), 3f)
                // Bottom-Right
                drawLine(color, Offset(size.width, size.height), Offset(size.width - bracketLen, size.height), 3f)
                drawLine(color, Offset(size.width, size.height), Offset(size.width, size.height - bracketLen), 3f)
            }

            // AI Bounding Box Label
            if (aiEnabled) {
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(NeonCyan)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "OBSTACLE DETECTED [${tofDistance}cm]",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = CyberDarkBg,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SnapshotThumbnail(snapshot: RobotSnapshot) {
    Box(
        modifier = Modifier
            .size(64.dp, 54.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0F1B30))
            .border(1.dp, CyberGlassBorder, RoundedCornerShape(8.dp))
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "SNAP",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "${snapshot.tofDistance}cm",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
