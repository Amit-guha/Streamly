package com.example.streamly.feature.player.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.streamly.R
import com.example.streamly.feature.player.presentation.contract.PlayerIntent
import com.example.streamly.feature.player.presentation.model.VideoUiModel
import com.example.streamly.ui.theme.StreamlyTheme

@Composable
internal fun VideoMetadataSection(
    video: VideoUiModel,
    isSubscribed: Boolean,
    onIntent: (PlayerIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = video.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = video.channelAvatarUrl,
                contentDescription = stringResource(R.string.player_channel_avatar_description),
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = video.channel, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(
                    text = stringResource(R.string.player_video_subtitle, video.views, video.uploadedAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Button(
                onClick = { onIntent(PlayerIntent.OnSubscribeClicked) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text(
                    text = stringResource(if (isSubscribed) R.string.player_subscribed else R.string.player_subscribe),
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Preview(name = "Mobile", device = Devices.PHONE)
@Composable
private fun VideoMetadataSectionPreview() {
    StreamlyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            VideoMetadataSection(
                video = VideoUiModel(
                    id = "1",
                    title = "Media3 in 10 minutes",
                    channel = "CodeLabs",
                    thumbnailUrl = "",
                    channelAvatarUrl = "https://i.pravatar.cc/150?img=3",
                    views = "512K views",
                    uploadedAt = "3 days ago",
                    videoUrl = "",
                ),
                isSubscribed = false,
                onIntent = {},
            )
        }
    }
}