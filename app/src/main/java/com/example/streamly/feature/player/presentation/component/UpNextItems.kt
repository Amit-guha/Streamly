package com.example.streamly.feature.player.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.streamly.R
import com.example.streamly.feature.player.presentation.contract.PlayerIntent
import com.example.streamly.feature.player.presentation.model.VideoUiModel
import com.example.streamly.ui.theme.StreamlyTheme

internal fun LazyListScope.upNextItems(
    upNext: List<VideoUiModel>,
    isLoading: Boolean,
    errorMessage: String?,
    onIntent: (PlayerIntent) -> Unit,
) {
    item { UpNextHeader() }
    when {
        isLoading && upNext.isEmpty() -> item { UpNextLoading() }
        errorMessage != null && upNext.isEmpty() -> item {
            UpNextError(onRetryClicked = { onIntent(PlayerIntent.OnRetryClicked) })
        }
        else -> items(upNext, key = { it.id }) { video ->
            UpNextVideoItem(video = video, onClick = { onIntent(PlayerIntent.OnUpNextVideoClicked(video)) })
        }
    }
}

@Composable
private fun UpNextHeader(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.player_up_next_title),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp),
    )
}

@Composable
private fun UpNextLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun UpNextError(onRetryClicked: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(stringResource(R.string.player_up_next_error))
        Button(onClick = onRetryClicked) {
            Text(stringResource(R.string.player_retry))
        }
    }
}

// upNextItems is a LazyListScope extension, not a @Composable — preview it hosted in a LazyColumn.
@Preview(name = "Mobile", device = Devices.PHONE)
@Composable
private fun UpNextItemsPreview() {
    StreamlyTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                upNextItems(
                    upNext = listOf(
                        VideoUiModel(
                            id = "2",
                            title = "Jetpack Compose Navigation 3 Deep Dive",
                            channel = "Android Academy",
                            thumbnailUrl = "https://picsum.photos/seed/player2/640/360",
                            channelAvatarUrl = "https://i.pravatar.cc/150?img=4",
                            views = "128K views",
                            uploadedAt = "1 week ago",
                            videoUrl = "",
                        ),
                        VideoUiModel(
                            id = "3",
                            title = "Hilt Dependency Injection Crash Course",
                            channel = "Compose Hub",
                            thumbnailUrl = "https://picsum.photos/seed/player3/640/360",
                            channelAvatarUrl = "https://i.pravatar.cc/150?img=5",
                            views = "89K views",
                            uploadedAt = "2 weeks ago",
                            videoUrl = "",
                        ),
                    ),
                    isLoading = false,
                    errorMessage = null,
                    onIntent = {},
                )
            }
        }
    }
}