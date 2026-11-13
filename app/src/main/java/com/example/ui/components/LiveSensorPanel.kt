package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.SettingsRemote
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SensorData
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.ElectricTeal
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LiveSensorPanel(
    sensorData: SensorData,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.testTag("live_sensor_panel"),
        cornerRadius = 20.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Radar,
                        contentDescription = "Live Telemetry",
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LIVE SENSOR TELEMETRY",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        letterSpacing = 1.2.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(StatusGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "REALTIME",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusGreen,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. ToF Laser Distance Sensor Bar
            val tofProgress = (sensorData.tof / 200f).coerceIn(0f, 1f)
            val tofColor by animateColorAsState(
                targetValue = when {
                    sensorData.tof < 25 -> StatusRed
                    sensorData.tof < 50 -> StatusYellow
                    else -> NeonCyan
                },
                label = "TofColor"
            )

            SensorMetricRow(
                icon = Icons.Default.Radar,
                label = "ToF Distance",
                valueText = "${sensorData.tof} cm",
                detailText = if (sensorData.tof < 25) "CRITICAL PROXIMITY" else "Path Clear",
                accentColor = tofColor,
                progress = tofProgress
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Ambient Light (LDR) percentage
            SensorMetricRow(
                icon = Icons.Default.LightMode,
                label = "Ambient Light (LDR)",
                valueText = "${sensorData.ldrPercentage}%",
                detailText = "Raw: ${sensorData.ldr}",
                accentColor = ElectricTeal,
                progress = sensorData.ldrPercentage / 100f
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 3. WiFi RSSI Signal
            val wifiPercent = ((sensorData.wifiRssi + 100) / 70f).coerceIn(0f, 1f)
            SensorMetricRow(
                icon = Icons.Default.Wifi,
                label = "WiFi Signal (RSSI)",
                valueText = "${sensorData.wifiRssi} dBm",
                detailText = "${(wifiPercent * 100).toInt()}% Signal",
                accentColor = StatusGreen,
                progress = wifiPercent
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 4. Battery Level
            val batColor = when {
                sensorData.battery > 30 -> StatusGreen
                sensorData.battery > 15 -> StatusYellow
                else -> StatusRed
            }
            SensorMetricRow(
                icon = Icons.Default.BatteryChargingFull,
                label = "Robot Battery",
                valueText = "${sensorData.battery}%",
                detailText = "Galaxy J2 Power Bank",
                accentColor = batColor,
                progress = sensorData.battery / 100f
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 5. Latest IR Code Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF070E1A))
                    .border(1.dp, CyberGlassBorder, RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SettingsRemote,
                            contentDescription = "IR Code",
                            tint = NeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LATEST IR DECODER:",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Text(
                        text = sensorData.ir,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NeonCyan,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
private fun SensorMetricRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    valueText: String,
    detailText: String,
    accentColor: Color,
    progress: Float
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = valueText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "($detailText)",
                    fontSize = 10.sp,
                    color = TextSecondary,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFF09101C))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0.02f, 1f))
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                accentColor.copy(alpha = 0.6f),
                                accentColor
                            )
                        )
                    )
            )
        }
    }
}
