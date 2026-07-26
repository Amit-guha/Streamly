package com.example.streamly.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex

/**
 * Full-screen loading spinner meant to sit on top of a screen's own content as an overlay
 * (e.g. `Box { ScreenContent(); if (uiState.isLoading) CircularCommonLoader() }`).
 *
 * Draws above whatever else is in the same [Box] via [zIndex], and — when
 * [isBlockingInteraction] is true (the default) — swallows taps so they never reach the content
 * underneath while it's showing.
 */
@Composable
fun CircularCommonLoader(
    modifier: Modifier = Modifier,
    indicatorColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = Color.Transparent,
    isBlockingInteraction: Boolean = true,
    zIndex: Float = 1f,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .zIndex(zIndex)
            .background(backgroundColor)
            .then(
                if (isBlockingInteraction) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {},
                    )
                } else {
                    Modifier
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = indicatorColor)
    }
}