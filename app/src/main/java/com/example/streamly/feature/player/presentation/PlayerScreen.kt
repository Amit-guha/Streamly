package com.example.streamly.feature.player.presentation

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import com.example.streamly.R
import com.example.streamly.core.designsystem.LocalWindowSizeClass
import com.example.streamly.core.navigation.NavigationDestination
import com.example.streamly.feature.downloads.presentation.navigation.DownloadsNavKey
import com.example.streamly.feature.player.presentation.component.ActionButtonsRow
import com.example.streamly.feature.player.presentation.component.DownloadOptionsBottomSheet
import com.example.streamly.feature.player.presentation.component.PlayerSurface
import com.example.streamly.feature.player.presentation.component.UpNextHeader
import com.example.streamly.feature.player.presentation.component.VideoMetadataSection
import com.example.streamly.feature.player.presentation.component.upNextItems
import com.example.streamly.feature.player.presentation.contract.PlayerEffect
import com.example.streamly.feature.player.presentation.contract.PlayerIntent
import com.example.streamly.feature.player.presentation.contract.PlayerUiState
import com.example.streamly.feature.player.presentation.model.VideoUiModel
import com.example.streamly.ui.theme.StreamlyTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
internal fun PlayerScreenRoute(
    videoId: String,
    title: String,
    videoUrl: String,
    onBack: () -> Unit,
    onNavigate: (NavigationDestination) -> Unit,
    onNavigateToDownloadsAfterDelete: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val windowSizeClass = LocalWindowSizeClass.current
    val snackbarHostState = remember { SnackbarHostState() }
    val downloadStartedMessage = stringResource(R.string.player_download_started_message)
    val viewActionLabel = stringResource(R.string.player_download_started_view_action)


    var isPlayerVisible by rememberSaveable { mutableStateOf(true) }

    BackHandler {
        isPlayerVisible = false
        viewModel.onIntent(PlayerIntent.OnBackRequested)
        onBack()
    }

    LaunchedEffect(Unit) {
        viewModel.onIntent(PlayerIntent.OnScreenStarted(videoId = videoId, title = title, videoUrl = videoUrl))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is PlayerEffect.ShareVideo -> {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, effect.title)
                        putExtra(Intent.EXTRA_TEXT, effect.videoUrl)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, null))
                }
                PlayerEffect.ShowDownloadStartedSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = downloadStartedMessage,
                        actionLabel = viewActionLabel,
                        duration = SnackbarDuration.Short,
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        onNavigate(DownloadsNavKey)
                    }
                }
                PlayerEffect.NavigateToDownloads -> {
                    isPlayerVisible = false
                    viewModel.onIntent(PlayerIntent.OnBackRequested)
                    onNavigateToDownloadsAfterDelete()
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> viewModel.onIntent(PlayerIntent.OnLifecyclePaused)
                Lifecycle.Event.ON_RESUME -> viewModel.onIntent(PlayerIntent.OnLifecycleResumed)
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        PlayerScreen(
            uiState = uiState,
            player = if (isPlayerVisible) viewModel.player else null,
            windowSizeClass = windowSizeClass,
            onIntent = viewModel::onIntent,
            onBack = {
                isPlayerVisible = false
                viewModel.onIntent(PlayerIntent.OnBackRequested)
                onBack()
            },
        )
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.systemBars),
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun PlayerScreen(
    uiState: PlayerUiState,
    player: Player?,
    windowSizeClass: WindowSizeClass,
    onIntent: (PlayerIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isCompactHeight = windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact
    val isTwoPane = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded && !isCompactHeight

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        if (isCompactHeight) {
            PlayerSurface(
                player = player,
                isMuted = uiState.isMuted,
                onBack = onBack,
                onIntent = onIntent,
                modifier = Modifier.fillMaxSize(),
                useFixedAspectRatio = false,
            )
        } else if (isTwoPane) {
            Row(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .windowInsetsPadding(WindowInsets.navigationBars),
                ) {
                    PlayerSurface(
                        player = player,
                        isMuted = uiState.isMuted,
                        onBack = onBack,
                        onIntent = onIntent,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    VideoMetadataSection(
                        video = uiState.video,
                        isSubscribed = uiState.isSubscribed,
                        onIntent = onIntent,
                    )
                    ActionButtonsRow(
                        isLiked = uiState.isLiked,
                        downloadStatus = uiState.downloadStatus,
                        onIntent = onIntent,
                    )
                }
                LazyColumn(
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxHeight()
                        .windowInsetsPadding(WindowInsets.navigationBars),
                ) {
                    upNextItems(
                        upNext = uiState.upNext,
                        isLoading = uiState.isLoadingUpNext,
                        errorMessage = uiState.upNextErrorMessage,
                        onIntent = onIntent,
                    )
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                PlayerSurface(
                    player = player,
                    isMuted = uiState.isMuted,
                    onBack = onBack,
                    onIntent = onIntent,
                    modifier = Modifier.fillMaxWidth(),
                )
                VideoMetadataSection(
                    video = uiState.video,
                    isSubscribed = uiState.isSubscribed,
                    onIntent = onIntent,
                )
                ActionButtonsRow(
                    isLiked = uiState.isLiked,
                    downloadStatus = uiState.downloadStatus,
                    onIntent = onIntent,
                )
                UpNextHeader()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .windowInsetsPadding(WindowInsets.navigationBars),
                ) {
                    upNextItems(
                        upNext = uiState.upNext,
                        isLoading = uiState.isLoadingUpNext,
                        errorMessage = uiState.upNextErrorMessage,
                        onIntent = onIntent,
                        includeHeader = false,
                    )
                }
            }
        }
    }

    if (uiState.showDownloadOptionsSheet) {
        DownloadOptionsBottomSheet(
            onRemoveClicked = { onIntent(PlayerIntent.OnRemoveDownloadClicked) },
            onDismiss = { onIntent(PlayerIntent.OnDownloadSheetDismissed) },
        )
    }
}

private val sampleVideo = VideoUiModel(
    id = "1",
    title = "Media3 in 10 minutes",
    channel = "CodeLabs",
    thumbnailUrl = "https://picsum.photos/seed/player1/640/360",
    channelAvatarUrl = "https://i.pravatar.cc/150?img=3",
    views = "512K views",
    uploadedAt = "3 days ago",
    videoUrl = "",
)

private val sampleUpNext = listOf(
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
)

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(name = "Mobile", device = Devices.PHONE)
@Composable
private fun PlayerScreenMobilePreview() {
    StreamlyTheme {
        PlayerScreen(
            uiState = PlayerUiState(video = sampleVideo, upNext = sampleUpNext),
            player = null,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
            onIntent = {},
            onBack = {},
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(name = "Foldable", device = Devices.FOLDABLE)
@Composable
private fun PlayerScreenFoldablePreview() {
    StreamlyTheme {
        PlayerScreen(
            uiState = PlayerUiState(video = sampleVideo, upNext = sampleUpNext),
            player = null,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(673.dp, 841.dp)),
            onIntent = {},
            onBack = {},
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(name = "Tablet", device = Devices.TABLET)
@Composable
private fun PlayerScreenTabletPreview() {
    StreamlyTheme {
        PlayerScreen(
            uiState = PlayerUiState(video = sampleVideo, upNext = sampleUpNext),
            player = null,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(1280.dp, 800.dp)),
            onIntent = {},
            onBack = {},
        )
    }
}