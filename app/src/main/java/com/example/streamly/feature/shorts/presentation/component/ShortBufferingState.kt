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
//
// Also true while STATE_IDLE with a playerError set — a load failure (e.g. no internet) exhausts
// ExoPlayer's own retry/backoff and parks the player there, distinct from a normal not-yet-
// prepared idle state (which has no error). Without this, the spinner would vanish the moment
// ExoPlayer gives up retrying, leaving a silently frozen frame for as long as the outage lasts,
// instead of staying up until playback actually resumes.
@androidx.annotation.OptIn(markerClass = [UnstableApi::class])
@Composable
internal fun rememberIsBuffering(player: Player): Boolean {
    fun isBufferingOrStalled(current: Player) = current.playbackState == Player.STATE_BUFFERING ||
        (current.playbackState == Player.STATE_IDLE && current.playerError != null)

    var isBuffering by remember(player) { mutableStateOf(isBufferingOrStalled(player)) }
    LaunchedEffect(player) {
        player.observeState(Player.EVENT_PLAYBACK_STATE_CHANGED, Player.EVENT_PLAYER_ERROR) { current ->
            isBuffering = isBufferingOrStalled(current)
        }.observe()
    }
    return isBuffering
}