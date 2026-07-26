package com.example.streamly.feature.player.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.streamly.R
import com.example.streamly.feature.player.presentation.model.VideoUiModel
import com.example.streamly.ui.theme.StreamlyTheme

@Composable
internal fun UpNextVideoItem(video: VideoUiModel, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AsyncImage(
            model = video.thumbnailUrl,
            contentDescription = stringResource(R.string.player_up_next_thumbnail_description),
            modifier = Modifier
                .width(140.dp)
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
        )

        Column {
            Text(
                text = video.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = stringResource(R.string.player_video_subtitle, video.views, video.uploadedAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(name = "Mobile", device = Devices.PHONE)
@Composable
private fun UpNextVideoItemPreview() {
    StreamlyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            UpNextVideoItem(
                video = VideoUiModel(
                    id = "2",
                    title = "Jetpack Compose Navigation 3 Deep Dive",
                    channel = "Android Academy",
                    thumbnailUrl = "https://picsum.photos/seed/player2/640/360",
                    channelAvatarUrl = "https://i.pravatar.cc/150?img=4",
                    views = "128K views",
                    uploadedAt = "1 week ago",
                    videoUrl = "",
                ),
                onClick = {},
            )
        }
    }
}