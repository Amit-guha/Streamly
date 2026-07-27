package com.example.streamly.feature.shorts.presentation.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.indicators.ProgressIndicator

private const val PROGRESS_TICK_COUNT = 1_000

/** Draggable seek bar backed by Media3 Compose's [ProgressIndicator] state — [player]'s position
 * both drives and is driven by the slider, matching the state holder's own seek contract
 * ([androidx.media3.ui.compose.state.ProgressStateWithTickCount.updateCurrentPositionProgress]
 * seeks the player directly, there's no separate "commit" step). */
@androidx.annotation.OptIn(markerClass = [UnstableApi::class])
@Composable
internal fun ShortProgressBar(player: Player, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()

    ProgressIndicator(player = player, totalTickCount = PROGRESS_TICK_COUNT, scope = scope) {
        Slider(
            value = currentPositionProgress,
            onValueChange = { progress -> updateCurrentPositionProgress(progress) },
            enabled = changingProgressEnabled,
            modifier = modifier,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
            ),
        )
    }
}