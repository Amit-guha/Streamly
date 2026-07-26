package com.example.streamly.core.designsystem

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Computed once in `MainActivity` — the one place guaranteed to hold a real `Activity` — and
 * provided down through the composition from there. Screens read [LocalWindowSizeClass] directly
 * instead of each resolving `LocalActivity` + `calculateWindowSizeClass` themselves.
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
val LocalWindowSizeClass = staticCompositionLocalOf<WindowSizeClass> {
    error(
        "LocalWindowSizeClass not provided — wrap the composition root with " +
            "CompositionLocalProvider(LocalWindowSizeClass provides ...)",
    )
}