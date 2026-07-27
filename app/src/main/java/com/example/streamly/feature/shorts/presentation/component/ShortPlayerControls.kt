package com.example.streamly.feature.shorts.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forward5
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay5
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.buttons.PlayPauseButton
import androidx.media3.ui.compose.buttons.SeekBackButton
import androidx.media3.ui.compose.buttons.SeekForwardButton
import com.example.streamly.R
import com.example.streamly.ui.theme.StreamlyTheme

/** Center transport row — 5s back, play/pause, 5s forward — all driven directly by [player] via
 * Media3's Compose button state holders.
 *
 * [player] is only nullable so the preview can render a static, non-functional row — the real
 * caller ([ShortPagerItem]) only ever invokes this once its pooled player is ready. */
@androidx.annotation.OptIn(markerClass = [UnstableApi::class])
@Composable
internal fun ShortPlayerControls(player: Player?, modifier: Modifier = Modifier) {
    if (player == null) {
        StaticTransportRow(modifier)
        return
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally),
    ) {
        SeekBackButton(player = player) {
            TransportButton(
                onClick = ::onClick,
                enabled = isEnabled,
                size = 44.dp,
                icon = Icons.Filled.Replay5,
                contentDescription = stringResource(R.string.shorts_seek_backward),
            )
        }
        PlayPauseButton(player = player) {
            TransportButton(
                onClick = ::onClick,
                enabled = isEnabled,
                size = 64.dp,
                icon = if (showPlay) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                contentDescription = stringResource(
                    if (showPlay) R.string.shorts_play else R.string.shorts_pause,
                ),
            )
        }
        SeekForwardButton(player = player) {
            TransportButton(
                onClick = ::onClick,
                enabled = isEnabled,
                size = 44.dp,
                icon = Icons.Filled.Forward5,
                contentDescription = stringResource(R.string.shorts_seek_forward),
            )
        }
    }
}

@Composable
private fun StaticTransportRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally),
    ) {
        TransportButton(
            onClick = {},
            enabled = true,
            size = 44.dp,
            icon = Icons.Filled.Replay5,
            contentDescription = stringResource(R.string.shorts_seek_backward),
        )
        TransportButton(
            onClick = {},
            enabled = true,
            size = 64.dp,
            icon = Icons.Filled.PlayArrow,
            contentDescription = stringResource(R.string.shorts_play),
        )
        TransportButton(
            onClick = {},
            enabled = true,
            size = 44.dp,
            icon = Icons.Filled.Forward5,
            contentDescription = stringResource(R.string.shorts_seek_forward),
        )
    }
}

@Composable
private fun TransportButton(
    onClick: () -> Unit,
    enabled: Boolean,
    size: Dp,
    icon: ImageVector,
    contentDescription: String,
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(size)
            .background(Color.Black.copy(alpha = 0.35f), CircleShape),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(size * 0.55f),
        )
    }
}

@Preview(name = "Mobile")
@Composable
private fun ShortPlayerControlsPreview() {
    StreamlyTheme {
        Box(modifier = Modifier.background(Color.Black).padding(16.dp)) {
            ShortPlayerControls(player = null)
        }
    }
}