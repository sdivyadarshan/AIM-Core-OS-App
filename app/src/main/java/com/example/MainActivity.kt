package com.example

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.data.model.RobotEyeState
import com.example.ui.components.LauncherBanner
import com.example.ui.components.RobotHeaderBar
import com.example.ui.components.RobotNavigationBar
import com.example.ui.components.RobotTab
import com.example.ui.screens.AssistantScreen
import com.example.ui.screens.EngineeringScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.LiveFaceScreen
import com.example.ui.screens.RcScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.UnderstandingAimbotScreen
import com.example.ui.screens.VisionScreen
import com.example.ui.theme.AIMCoreOSTheme
import com.example.ui.theme.CyberDarkBg
import com.example.ui.viewmodel.RobotViewModel
import com.example.utils.TtsManager

class MainActivity : ComponentActivity() {

    private val viewModel: RobotViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        setContent {
            AIMCoreOSTheme {
                AimCoreOsApp(viewModel = viewModel)
            }
        }

        // Full Screen Immersive Mode (Hide status/navigation bars for dedicated robot OS)
        hideSystemUI()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

    private fun hideSystemUI() {
        runCatching {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val controller = WindowInsetsControllerCompat(window, window.decorView)
            controller.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}

@Composable
fun AimCoreOsApp(viewModel: RobotViewModel) {
    val context = LocalContext.current
    val ttsManager = remember { TtsManager(context) }
    var currentTab by remember { mutableStateOf(RobotTab.HOME) }

    DisposableEffect(Unit) {
        onDispose {
            ttsManager.shutdown()
        }
    }

    val settings by viewModel.settings.collectAsState()
    val sensorData by viewModel.sensorData.collectAsState()
    val eyeState by viewModel.eyeState.collectAsState()

    LaunchedEffect(settings.isLandscape) {
        val activity = context as? ComponentActivity
        activity?.requestedOrientation = if (settings.isLandscape) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    fun onSelectTab(tab: RobotTab) {
        currentTab = tab
        ttsManager.vocalizeTabChange(tab)
        when (tab) {
            RobotTab.HOME -> viewModel.setEyeState(RobotEyeState.IDLE)
            RobotTab.EXPLORE -> viewModel.setEyeState(RobotEyeState.EXPLORING)
            RobotTab.RC -> viewModel.setEyeState(RobotEyeState.RC_MODE)
            RobotTab.VISION -> viewModel.setEyeState(RobotEyeState.SCANNING)
            RobotTab.ASSISTANT -> viewModel.setEyeState(RobotEyeState.ASSISTANT_MODE)
            RobotTab.ENGINEERING -> viewModel.setEyeState(RobotEyeState.THINKING)
            RobotTab.SETTINGS -> viewModel.setEyeState(RobotEyeState.IDLE)
            RobotTab.UNDERSTANDING_AIMBOT -> viewModel.setEyeState(RobotEyeState.SCANNING)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberDarkBg),
        topBar = {
            if (currentTab != RobotTab.HOME) {
                RobotHeaderBar(
                    robotName = settings.robotName,
                    sensorData = sensorData,
                    eyeState = eyeState,
                    isMapping = settings.isMapping
                )
            }
        },
        bottomBar = {
            if (currentTab != RobotTab.HOME) {
                RobotNavigationBar(
                    currentTab = currentTab,
                    onTabSelected = { selectedTab -> onSelectTab(selectedTab) }
                )
            }
        },
        containerColor = CyberDarkBg
    ) { paddingValues ->
        Column(
            modifier = (if (currentTab == RobotTab.HOME) Modifier else Modifier.padding(paddingValues))
                .fillMaxSize()
        ) {
            // First Launch Home Setup Banner (Only show if not set as default launcher)
            if (currentTab != RobotTab.HOME) {
                LauncherBanner(
                    isDefaultLauncher = settings.isDefaultLauncher,
                    onAccept = { viewModel.setDefaultLauncher(true) }
                )
            }

            // Screen Transitions
            Crossfade(
                targetState = currentTab,
                animationSpec = tween(durationMillis = 200),
                label = "screenCrossfade",
                modifier = Modifier.weight(1f)
            ) { tab ->
                when (tab) {
                    RobotTab.HOME -> LiveFaceScreen(
                        viewModel = viewModel,
                        onTabSelected = { selectedTab -> onSelectTab(selectedTab) }
                    )
                    RobotTab.EXPLORE -> ExploreScreen(viewModel = viewModel)
                    RobotTab.RC -> RcScreen(viewModel = viewModel)
                    RobotTab.VISION -> VisionScreen(viewModel = viewModel)
                    RobotTab.ASSISTANT -> AssistantScreen(viewModel = viewModel)
                    RobotTab.ENGINEERING -> EngineeringScreen(viewModel = viewModel)
                    RobotTab.SETTINGS -> SettingsScreen(
                        viewModel = viewModel,
                        ttsManager = ttsManager,
                        onNavigateToAimbotGuide = { onSelectTab(RobotTab.UNDERSTANDING_AIMBOT) }
                    )
                    RobotTab.UNDERSTANDING_AIMBOT -> UnderstandingAimbotScreen(
                        viewModel = viewModel,
                        onBackToHome = { onSelectTab(RobotTab.HOME) }
                    )
                }
            }
        }
    }
}
