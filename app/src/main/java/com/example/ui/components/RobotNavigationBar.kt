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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextSecondary

enum class RobotTab(val title: String, val icon: ImageVector, val tag: String) {
    HOME("Face", Icons.Default.Face, "nav_tab_home"),
    EXPLORE("Explore", Icons.Default.Explore, "nav_tab_explore"),
    RC("RC Drive", Icons.Default.Games, "nav_tab_rc"),
    VISION("Vision", Icons.Default.CameraAlt, "nav_tab_vision"),
    ASSISTANT("Assistant", Icons.Default.AutoAwesome, "nav_tab_assistant"),
    ENGINEERING("Engineering", Icons.Default.BugReport, "nav_tab_engineering"),
    SETTINGS("Settings", Icons.Default.Settings, "nav_tab_settings"),
    UNDERSTANDING_AIMBOT("Understanding AIM-Bot", Icons.Default.Lightbulb, "nav_tab_aimbot_guide")
}

@Composable
fun RobotNavigationBar(
    currentTab: RobotTab,
    onTabSelected: (RobotTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CyberCardBg.copy(alpha = 0.95f))
            .border(1.dp, CyberGlassBorder, RoundedCornerShape(16.dp))
            .padding(vertical = 4.dp)
            .testTag("robot_navigation_bar")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RobotTab.values().forEach { tab ->
                val isSelected = tab == currentTab
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) NeonCyan.copy(alpha = 0.18f) else Color.Transparent)
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag(tab.tag),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title,
                        tint = if (isSelected) NeonCyan else TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tab.title,
                        fontSize = 8.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) NeonCyan else TextSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
