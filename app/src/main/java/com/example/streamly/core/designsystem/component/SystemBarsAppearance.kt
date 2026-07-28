package com.example.streamly.core.designsystem.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.streamly.core.common.extension.findActivity

@Composable
fun SystemBarsAppearance(lightStatusBarIcons: Boolean, lightNavigationBarIcons: Boolean = lightStatusBarIcons) {
    val view = LocalView.current
    if (view.isInEditMode) return
    val activity = LocalContext.current.findActivity() ?: return
    val controller = remember(activity, view) { WindowCompat.getInsetsController(activity.window, view) }

    SideEffect {
        controller.isAppearanceLightStatusBars = lightStatusBarIcons
        controller.isAppearanceLightNavigationBars = lightNavigationBarIcons
    }
}