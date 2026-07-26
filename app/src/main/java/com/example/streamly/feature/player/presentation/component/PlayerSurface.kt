package com.example.streamly.feature.player.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import com.example.streamly.R
import com.example.streamly.feature.player.presentation.contract.PlayerIntent
import com.example.streamly.ui.theme.StreamlyTheme

// PlayerView.setShowBuffering is marked @UnstableApi by Media3 (API may change between
// releases) but is the documented way to get a built-in buffering spinner; no replacement exists.
@androidx.annotation.OptIn(markerClass = [UnstableApi::class])
@Composable
internal fun PlayerSurface(
    player: Player?,
    isMuted: Boolean,
    onBack: () -> Unit,
    onIntent: (PlayerIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isPreview = LocalInspectionMode.current

    Box(
        modifier = modifier
            .aspectRatio(16f / 9f)
            .background(MaterialTheme.colorScheme.scrim),
    ) {
        if (player != null && !isPreview) {
            val boundPlayer = player
            AndroidView(
                factory = { context ->
                    PlayerView(context).apply {
                        this.player = boundPlayer
                        useController = true
                        setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                    }
                },
                modifier = Modifier.fillMaxSize(),
            )
        }

        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart)) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.player_back),
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }

        IconButton(
            onClick = { onIntent(PlayerIntent.OnMuteToggled) },
            modifier = Modifier.align(Alignment.TopEnd),
        ) {
            Icon(
                imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = stringResource(if (isMuted) R.string.player_unmute else R.string.player_mute),
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Preview(name = "Mobile", device = Devices.PHONE)
@Composable
private fun PlayerSurfacePreview() {
    StreamlyTheme {
        PlayerSurface(player = null, isMuted = false, onBack = {}, onIntent = {})
    }
}