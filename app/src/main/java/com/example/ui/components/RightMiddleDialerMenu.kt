package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.roundToInt

@Composable
fun RightMiddleDialerMenu(
    isOpen: Boolean,
    currentTab: RobotTab,
    onTabSelected: (RobotTab) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val tabs = RobotTab.values()
    var selectedIndex by remember(currentTab) {
        mutableIntStateOf(tabs.indexOf(currentTab).coerceAtLeast(0))
    }

    // Drag vertical threshold tracker
    var dragAccumulator by remember { mutableIntStateOf(0) }

    // Double tap timestamp tracker
    var lastTapTime by remember { mutableLongStateOf(0L) }
    var lastTappedIndex by remember { mutableIntStateOf(-1) }

    AnimatedVisibility(
        visible = isOpen,
        enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
        exit = fadeOut() + slideOutHorizontally(targetOffsetX = { it }),
        modifier = modifier.fillMaxSize()
    ) {
        // NO BACKGROUND BOX / SCRIM OVERLAY - Completely transparent background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = false) {}
        ) {
            // Right-Middle Frameless Floating Dialer Panel
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight(0.92f)
                    .width(280.dp)
                    .padding(end = 8.dp)
                    // NO BACKGROUND BOX - Completely transparent box container
                    .background(Color.Transparent)
                    .draggable(
                        orientation = Orientation.Vertical,
                        state = rememberDraggableState { delta ->
                            dragAccumulator += delta.roundToInt()
                            if (dragAccumulator < -35) { // Swipe up -> next option
                                selectedIndex = (selectedIndex + 1) % tabs.size
                                dragAccumulator = 0
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            } else if (dragAccumulator > 35) { // Swipe down -> previous option
                                selectedIndex = if (selectedIndex - 1 < 0) tabs.size - 1 else selectedIndex - 1
                                dragAccumulator = 0
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        }
                    )
                    .padding(8.dp)
                    .testTag("right_middle_dialer_menu")
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Header Bar (Floating Glass Pills)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xEE081629))
                            .border(1.dp, NeonCyan.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "AIM-BOT DIALER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NeonCyan,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "SWIPE / DOUBLE TAP TO OPEN",
                                fontSize = 7.5.sp,
                                color = TextSecondary,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        IconButton(
                            onClick = onClose,
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF142942))
                                .testTag("dialer_close_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Dialer",
                                tint = TextPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    // Up Arrow Controls
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedIndex = if (selectedIndex - 1 < 0) tabs.size - 1 else selectedIndex - 1
                        },
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color(0xEE0F233B))
                            .border(1.dp, NeonCyan.copy(alpha = 0.5f), CircleShape)
                            .testTag("dialer_up_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Previous Option",
                            tint = NeonCyan
                        )
                    }

                    // Rotary Dial Stack (Showing 5 floating options around center focused item)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val indicesToShow = listOf(-2, -1, 0, 1, 2)

                        indicesToShow.forEach { offset ->
                            val itemIndex = (selectedIndex + offset + tabs.size * 10) % tabs.size
                            val tab = tabs[itemIndex]
                            val isFocused = offset == 0

                            val scale by animateFloatAsState(
                                targetValue = if (isFocused) 1.12f else 0.85f,
                                animationSpec = tween(140),
                                label = "dialerScale"
                            )

                            val alpha by animateFloatAsState(
                                targetValue = when (offset) {
                                    0 -> 1.0f
                                    -1, 1 -> 0.75f
                                    else -> 0.35f
                                },
                                animationSpec = tween(140),
                                label = "dialerAlpha"
                            )

                            val horizontalShift = when (offset) {
                                0 -> 0.dp
                                -1, 1 -> 14.dp
                                else -> 28.dp
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = horizontalShift)
                                    .graphicsLayer {
                                        scaleX = scale
                                        scaleY = scale
                                        this.alpha = alpha
                                    }
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (isFocused) NeonCyan.copy(alpha = 0.35f)
                                        else Color(0xDD0C1D30)
                                    )
                                    .border(
                                        width = if (isFocused) 2.dp else 1.dp,
                                        color = if (isFocused) NeonCyan else CyberGlassBorder,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .pointerInput(itemIndex) {
                                        detectTapGestures(
                                            onDoubleTap = {
                                                // DOUBLE TAP IMMEDIATELY OPENS OPTION!
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                onTabSelected(tab)
                                                onClose()
                                            },
                                            onTap = {
                                                val now = System.currentTimeMillis()
                                                if (lastTappedIndex == itemIndex && now - lastTapTime < 500) {
                                                    // Quick second tap on same item -> OPEN OPTION!
                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    onTabSelected(tab)
                                                    onClose()
                                                } else {
                                                    // First tap -> Focus this item
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                    selectedIndex = itemIndex
                                                    lastTappedIndex = itemIndex
                                                    lastTapTime = now
                                                }
                                            }
                                        )
                                    }
                                    .padding(horizontal = 10.dp, vertical = if (isFocused) 8.dp else 5.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(if (isFocused) 30.dp else 22.dp)
                                                .clip(CircleShape)
                                                .background(if (isFocused) NeonCyan else Color(0xFF163254)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = tab.icon,
                                                contentDescription = tab.title,
                                                tint = if (isFocused) CyberDarkBg else TextPrimary,
                                                modifier = Modifier.size(if (isFocused) 16.dp else 12.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Column {
                                            Text(
                                                text = tab.title.uppercase(),
                                                fontSize = if (isFocused) 11.5.sp else 9.5.sp,
                                                fontWeight = if (isFocused) FontWeight.ExtraBold else FontWeight.Medium,
                                                color = if (isFocused) NeonCyan else TextPrimary,
                                                fontFamily = FontFamily.Monospace
                                            )

                                            if (isFocused) {
                                                Text(
                                                    text = "DOUBLE TAP TO OPEN PAGE",
                                                    fontSize = 7.5.sp,
                                                    color = StatusGreen,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }

                                    if (isFocused) {
                                        Box(
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .background(NeonCyan)
                                                .padding(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "Select",
                                                tint = CyberDarkBg,
                                                modifier = Modifier.size(10.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Down Arrow Controls
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedIndex = (selectedIndex + 1) % tabs.size
                        },
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color(0xEE0F233B))
                            .border(1.dp, NeonCyan.copy(alpha = 0.5f), CircleShape)
                            .testTag("dialer_down_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Next Option",
                            tint = NeonCyan
                        )
                    }

                    // Floating Double-Tap Activate Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(NeonCyan)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onTabSelected(tabs[selectedIndex])
                                onClose()
                            }
                            .padding(vertical = 9.dp)
                            .testTag("dialer_activate_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TouchApp,
                                contentDescription = null,
                                tint = CyberDarkBg,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "OPEN ${tabs[selectedIndex].title.uppercase()}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CyberDarkBg,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
