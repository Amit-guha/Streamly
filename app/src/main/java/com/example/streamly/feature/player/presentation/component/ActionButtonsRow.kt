package com.example.streamly.feature.player.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.streamly.R
import com.example.streamly.feature.player.presentation.contract.PlayerIntent
import com.example.streamly.ui.theme.StreamlyTheme

@Composable
internal fun ActionButtonsRow(
    isLiked: Boolean,
    onIntent: (PlayerIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ActionPill(
            icon = Icons.Filled.ThumbUp,
            label = stringResource(if (isLiked) R.string.player_liked else R.string.player_like),
            isActive = isLiked,
            onClick = { onIntent(PlayerIntent.OnLikeClicked) },
        )
        ActionPill(
            icon = Icons.Filled.Share,
            label = stringResource(R.string.player_share),
            isActive = false,
            onClick = { onIntent(PlayerIntent.OnShareClicked) },
        )
    }
}

@Composable
private fun ActionPill(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor = if (isActive) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = if (isActive) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColor),
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label)
    }
}

@Preview(name = "Mobile", device = Devices.PHONE)
@Composable
private fun ActionButtonsRowPreview() {
    StreamlyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ActionButtonsRow(isLiked = true, onIntent = {})
        }
    }
}