package com.example.streamly.feature.shorts.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.state.observeState

// Media3 Compose UI has ready-made state holders for play/pause/progress but not buffering, so
// this is built directly on the same Player.observeState(...) utility those are built on.
@androidx.annotation.OptIn(markerClass = [UnstableApi::class])
@Composable
internal fun rememberIsBuffering(player: Player): Boolean {
    var isBuffering by remember(player) { mutableStateOf(player.playbackState == Player.STATE_BUFFERING) }
    LaunchedEffect(player) {
        player.observeState(Player.EVENT_PLAYBACK_STATE_CHANGED) { current ->
            isBuffering = current.playbackState == Player.STATE_BUFFERING
        }.observe()
    }
    return isBuffering
}