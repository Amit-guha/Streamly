package com.example.streamly.feature.downloads.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import com.example.streamly.core.common.enum.DownloadStatus
import com.example.streamly.feature.downloads.domain.model.DownloadItem
import com.example.streamly.ui.theme.StreamlyTheme

@Composable
internal fun DownloadListItem(
    download: DownloadItem,
    onClick: () -> Unit,
    onRemoveClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isPlayable = download.status == DownloadStatus.COMPLETED
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = isPlayable, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(width = 96.dp, height = 64.dp)
                .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp)),
        ) {
            if (!download.thumbnailUrl.isNullOrBlank()) {
                AsyncImage(
                    model = download.thumbnailUrl,
                    contentDescription = stringResource(R.string.downloads_thumbnail_description),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = download.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            DownloadStatusIndicator(download = download)
        }

        IconButton(onClick = onRemoveClicked) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(R.string.downloads_remove_description),
            )
        }
    }
}

@Composable
private fun DownloadStatusIndicator(download: DownloadItem, modifier: Modifier = Modifier) {
    when (download.status) {
        DownloadStatus.DOWNLOADING, DownloadStatus.PENDING -> LinearProgressIndicator(
            progress = { download.progressPercent / 100f },
            modifier = modifier.fillMaxWidth(),
        )
        DownloadStatus.COMPLETED -> Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(color = MaterialTheme.colorScheme.tertiary, shape = CircleShape),
            )
            Text(
                text = stringResource(R.string.downloads_status_ready),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
        DownloadStatus.FAILED -> Text(
            text = stringResource(R.string.downloads_status_failed),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
        )
        DownloadStatus.PAUSED -> Text(
            text = stringResource(R.string.downloads_status_pending),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(name = "Mobile", device = Devices.PHONE)
@Composable
private fun DownloadListItemDownloadingPreview() {
    StreamlyTheme {
        DownloadListItem(
            download = DownloadItem(
                videoId = "1",
                title = "Weekly recap",
                thumbnailUrl = "https://picsum.photos/seed/download1/640/360",
                videoUrl = "",
                status = DownloadStatus.DOWNLOADING,
                progressPercent = 62,
                downloadedBytes = 62_000_000L,
                totalBytes = 100_000_000L,
            ),
            onClick = {},
            onRemoveClicked = {},
        )
    }
}

@Preview(name = "Ready", device = Devices.PHONE)
@Composable
private fun DownloadListItemReadyPreview() {
    StreamlyTheme {
        DownloadListItem(
            download = DownloadItem(
                videoId = "2",
                title = "Media3 in 10 min",
                thumbnailUrl = "https://picsum.photos/seed/download2/640/360",
                videoUrl = "https://example.com/video.m3u8",
                status = DownloadStatus.COMPLETED,
                progressPercent = 100,
                downloadedBytes = 100_000_000L,
                totalBytes = 100_000_000L,
            ),
            onClick = {},
            onRemoveClicked = {},
        )
    }
}