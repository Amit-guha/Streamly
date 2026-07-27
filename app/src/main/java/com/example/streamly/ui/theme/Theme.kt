package com.example.streamly.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Light and Dark schemes are intentionally identical — Streamly uses one fixed
// color scheme regardless of system light/dark setting.
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
