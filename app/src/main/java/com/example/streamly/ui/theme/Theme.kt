package com.example.streamly.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Light and Dark schemes are intentionally identical — Streamly uses one fixed
// color scheme regardless of system light/dark setting.
//
// Every role actually referenced elsewhere in the app (via MaterialTheme.colorScheme.*) must be
// listed explicitly in BOTH schemes below. darkColorScheme()/lightColorScheme() fall back to
// Material3's own differing built-in defaults for any role left unset, which silently breaks the
// "one fixed scheme" guarantee for that role even though the ones listed here stay in sync -
// e.g. onSurfaceVariant previously fell back to Material3's dark-theme default (a light gray
// meant for text on a dark background) while the app's actual background stayed the light
// StreamlySurface, producing washed-out secondary text and chip labels in system dark mode.
private val DarkColorScheme = darkColorScheme(
    primary = StreamlyBlue,
    onPrimary = StreamlyOnBlue,
    primaryContainer = StreamlyBlueContainer,
    onPrimaryContainer = StreamlyOnBlue,
    secondary = StreamlyBlueDeep,
    onSecondary = StreamlyOnBlue,
    tertiary = StreamlyGreen,
    onTertiary = StreamlyOnGreen,
    background = StreamlySurface,
    onBackground = StreamlyOnSurface,
    surface = StreamlySurface,
    onSurface = StreamlyOnSurface,
    surfaceVariant = StreamlySurfaceVariant,
    onSurfaceVariant = StreamlyOnSurfaceVariant,
    outlineVariant = StreamlyOutlineVariant,
    error = StreamlyError,
    onError = StreamlyOnError,
)

private val LightColorScheme = lightColorScheme(
    primary = StreamlyBlue,
    onPrimary = StreamlyOnBlue,
    primaryContainer = StreamlyBlueContainer,
    onPrimaryContainer = StreamlyOnBlue,
    secondary = StreamlyBlueDeep,
    onSecondary = StreamlyOnBlue,
    tertiary = StreamlyGreen,
    onTertiary = StreamlyOnGreen,
    background = StreamlySurface,
    onBackground = StreamlyOnSurface,
    surface = StreamlySurface,
    onSurface = StreamlyOnSurface,
    surfaceVariant = StreamlySurfaceVariant,
    onSurfaceVariant = StreamlyOnSurfaceVariant,
    outlineVariant = StreamlyOutlineVariant,
    error = StreamlyError,
    onError = StreamlyOnError,
)

@Composable
fun StreamlyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
