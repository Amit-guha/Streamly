package com.example.streamly.feature.player.presentation

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.example.streamly.core.designsystem.LocalWindowSizeClass
import com.example.streamly.feature.player.presentation.component.ActionButtonsRow
import com.example.streamly.feature.player.presentation.component.PlayerSurface
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
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val windowSizeClass = LocalWindowSizeClass.current
    var isExiting by remember { mutableStateOf(false) }

    BackHandler(enabled = !isExiting) {
        isExiting = true
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

    if (isExiting) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {}
    } else {
        PlayerScreen(
            uiState = uiState,
            player = viewModel.player,
            windowSizeClass = windowSizeClass,
            onIntent = viewModel::onIntent,
            onBack = {
                isExiting = true
                viewModel.onIntent(PlayerIntent.OnBackRequested)
                onBack()
            },
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
    // Two-pane needs width AND height to spare — a phone rotated to landscape is often
    // "Expanded" width alone (commonly >840dp) but "Compact" height, and would otherwise get
    // squeezed into a cramped tablet-style split layout instead of a normal scrolling screen.
    val isTwoPane = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded &&
        windowSizeClass.heightSizeClass != WindowHeightSizeClass.Compact

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        if (isTwoPane) {
            Row(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
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
                    ActionButtonsRow(isLiked = uiState.isLiked, onIntent = onIntent)
                }
                LazyColumn(modifier = Modifier.weight(0.4f).fillMaxHeight()) {
                    upNextItems(
                        upNext = uiState.upNext,
                        isLoading = uiState.isLoadingUpNext,
                        errorMessage = uiState.upNextErrorMessage,
                        onIntent = onIntent,
                    )
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    PlayerSurface(
                        player = player,
                        isMuted = uiState.isMuted,
                        onBack = onBack,
                        onIntent = onIntent,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                item {
                    VideoMetadataSection(
                        video = uiState.video,
                        isSubscribed = uiState.isSubscribed,
                        onIntent = onIntent,
                    )
                }
                item {
                    ActionButtonsRow(isLiked = uiState.isLiked, onIntent = onIntent)
                }
                upNextItems(
                    upNext = uiState.upNext,
                    isLoading = uiState.isLoadingUpNext,
                    errorMessage = uiState.upNextErrorMessage,
                    onIntent = onIntent,
                )
            }
        }
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