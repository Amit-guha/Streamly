package com.example.streamly.feature.shorts.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.streamly.R
import com.example.streamly.ui.theme.StreamlyTheme

/** Like/comment/share are visual-only stubs per spec — only mute is wired to real state, since
 * it needs to persist across the pooled players as the user swipes between shorts. */
@Composable
internal fun ShortActionRail(
    isMuted: Boolean,
    onMuteToggled: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        RailButton(icon = Icons.Filled.FavoriteBorder, contentDescription = stringResource(R.string.shorts_like), onClick = {})
        RailButton(icon = Icons.AutoMirrored.Filled.Comment, contentDescription = stringResource(R.string.shorts_comment), onClick = {})
        RailButton(icon = Icons.Filled.Share, contentDescription = stringResource(R.string.shorts_share), onClick = {})
        RailButton(
            icon = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
            contentDescription = stringResource(if (isMuted) R.string.shorts_unmute else R.string.shorts_mute),
            onClick = onMuteToggled,
        )
    }
}

@Composable
private fun RailButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .background(Color.Black.copy(alpha = 0.35f), CircleShape),
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription, tint = Color.White)
    }
}

@Preview(name = "Mobile")
@Composable
private fun ShortActionRailPreview() {
    StreamlyTheme {
        Box(modifier = Modifier.background(Color.Black).padding(16.dp)) {
            ShortActionRail(isMuted = false, onMuteToggled = {})
        }
    }
}