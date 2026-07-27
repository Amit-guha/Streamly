package com.example.streamly.feature.downloads.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.streamly.R
import com.example.streamly.core.common.enum.DownloadStatus
import com.example.streamly.core.designsystem.LocalWindowSizeClass
import com.example.streamly.feature.downloads.domain.model.DownloadItem
import com.example.streamly.feature.downloads.presentation.component.DownloadListItem
import com.example.streamly.feature.downloads.presentation.contract.DownloadsEffect
import com.example.streamly.feature.downloads.presentation.contract.DownloadsIntent
import com.example.streamly.feature.downloads.presentation.contract.DownloadsUiState
import com.example.streamly.ui.theme.StreamlyTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
internal fun DownloadsScreenRoute(
    onBack: () -> Unit,
    onNavigateToPlayer: (videoId: String, title: String, videoUrl: String) -> Unit,
    viewModel: DownloadsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val windowSizeClass = LocalWindowSizeClass.current

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                DownloadsEffect.NavigateBack -> onBack()
                is DownloadsEffect.NavigateToPlayer -> onNavigateToPlayer(
                    effect.videoId,
                    effect.title,
                    effect.videoUrl,
                )
            }
        }
    }

    DownloadsScreen(
        uiState = uiState,
        windowSizeClass = windowSizeClass,
        onIntent = viewModel::onIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun DownloadsScreen(
    uiState: DownloadsUiState,
    windowSizeClass: WindowSizeClass,
    onIntent: (DownloadsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isWideLayout = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.downloads_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { onIntent(DownloadsIntent.OnBackClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.downloads_back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (isWideLayout) Modifier.widthIn(max = 640.dp) else Modifier),
            ) {
                Text(
                    text = stringResource(
                        R.string.downloads_storage_used,
                        formatBytes(uiState.usedStorageBytes),
                        formatBytes(uiState.totalStorageBytes),
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )

                if (uiState.downloads.isEmpty()) {
                    Text(
                        text = stringResource(R.string.downloads_empty_message),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                    )
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(uiState.downloads, key = { it.videoId }) { download ->
                            DownloadListItem(
                                download = download,
                                onClick = { onIntent(DownloadsIntent.OnItemClicked(download)) },
                                onRemoveClicked = { onIntent(DownloadsIntent.OnRemoveClicked(download.videoId)) },
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatBytes(bytes: Long): String {
    val gigabytes = bytes / 1_000_000_000.0
    return "%.1f GB".format(gigabytes)
}

private val sampleDownloads = listOf(
    DownloadItem(
        videoId = "1",
        title = "Weekly recap",
        thumbnailUrl = "https://picsum.photos/seed/download1/640/360",
        videoUrl = "",
        status = DownloadStatus.DOWNLOADING,
        progressPercent = 62,
        downloadedBytes = 620_000_000L,
        totalBytes = 1_000_000_000L,
    ),
    DownloadItem(
        videoId = "2",
        title = "Media3 in 10 min",
        thumbnailUrl = "https://picsum.photos/seed/download2/640/360",
        videoUrl = "https://example.com/video.m3u8",
        status = DownloadStatus.COMPLETED,
        progressPercent = 100,
        downloadedBytes = 350_000_000L,
        totalBytes = 350_000_000L,
    ),
)

private val sampleUiState = DownloadsUiState(
    downloads = sampleDownloads,
    usedStorageBytes = 1_200_000_000L,
    totalStorageBytes = 8_000_000_000L,
)

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(name = "Mobile", device = Devices.PHONE)
@Composable
private fun DownloadsScreenMobilePreview() {
    StreamlyTheme {
        DownloadsScreen(
            uiState = sampleUiState,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
            onIntent = {},
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(name = "Foldable", device = Devices.FOLDABLE)
@Composable
private fun DownloadsScreenFoldablePreview() {
    StreamlyTheme {
        DownloadsScreen(
            uiState = sampleUiState,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(673.dp, 841.dp)),
            onIntent = {},
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(name = "Tablet", device = Devices.TABLET)
@Composable
private fun DownloadsScreenTabletPreview() {
    StreamlyTheme {
        DownloadsScreen(
            uiState = sampleUiState,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(1280.dp, 800.dp)),
            onIntent = {},
        )
    }
}