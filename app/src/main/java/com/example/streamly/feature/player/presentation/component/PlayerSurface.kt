package com.example.streamly.feature.player.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
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

@androidx.annotation.OptIn(markerClass = [UnstableApi::class])
@Composable
internal fun PlayerSurface(
    player: Player?,
    isMuted: Boolean,
    onBack: () -> Unit,
    onIntent: (PlayerIntent) -> Unit,
    modifier: Modifier = Modifier,
    useFixedAspectRatio: Boolean = true,
) {
    val isPreview = LocalInspectionMode.current

    Column(modifier = modifier) {
        if (useFixedAspectRatio) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
                    .background(MaterialTheme.colorScheme.scrim),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (useFixedAspectRatio) Modifier.aspectRatio(16f / 9f) else Modifier.weight(
                        1f
                    )
                )
                .background(MaterialTheme.colorScheme.scrim),
        ) {
            // Fullscreen landscape IS the whole screen, so a cutout or the navigation bar can
            // land on any edge. Inset the video and its controls directly so neither renders
            // underneath them - this Box's own scrim (above) still reaches every physical edge,
            // showing through as a letterbox bar wherever this shrinks the content.
            val contentModifier = Modifier
                .fillMaxSize()
                .then(if (useFixedAspectRatio) Modifier else Modifier.windowInsetsPadding(WindowInsets.safeDrawing))

            Box(modifier = contentModifier) {
                if (player != null && !isPreview) {
                    AndroidView(
                        factory = { context ->
                            PlayerView(context).apply {
                                this.player = player
                                useController = true
                                setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                                setShowPreviousButton(false)
                                setShowNextButton(false)
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
    }
}

@Preview(name = "Mobile", device = Devices.PHONE)
@Composable
private fun PlayerSurfacePreview() {
    StreamlyTheme {
        PlayerSurface(player = null, isMuted = false, onBack = {}, onIntent = {})
    }
}