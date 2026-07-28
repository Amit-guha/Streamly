package com.example.streamly.feature.home.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.streamly.core.designsystem.LocalWindowSizeClass
import com.example.streamly.core.designsystem.component.CircularCommonLoader
import com.example.streamly.core.navigation.NavigationDestination
import com.example.streamly.feature.downloads.presentation.navigation.DownloadsNavKey
import com.example.streamly.feature.home.presentation.component.ErrorContent
import com.example.streamly.feature.home.presentation.component.HomeFeedContent
import com.example.streamly.feature.home.presentation.contract.HomeEffect
import com.example.streamly.feature.home.presentation.contract.HomeIntent
import com.example.streamly.feature.home.presentation.contract.HomeUiState
import com.example.streamly.feature.home.presentation.model.VideoUiModel
import com.example.streamly.feature.player.presentation.navigation.PlayerNavKey
import com.example.streamly.feature.profile.presentation.navigation.ProfileNavKey
import com.example.streamly.feature.shorts.presentation.navigation.ShortsNavKey
import com.example.streamly.ui.theme.StreamlyTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
internal fun HomeScreenRoute(
    onNavigate: (NavigationDestination) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val windowSizeClass = LocalWindowSizeClass.current

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is HomeEffect.NavigateToPlayer -> onNavigate(
                    PlayerNavKey(
                        videoId = effect.video.id,
                        title = effect.video.title,
                        videoUrl = effect.video.videoUrl,
                    ),
                )
                HomeEffect.NavigateToShorts -> onNavigate(ShortsNavKey)
                HomeEffect.NavigateToDownloads -> onNavigate(DownloadsNavKey)
                HomeEffect.NavigateToProfile -> onNavigate(ProfileNavKey)
            }
        }
    }

    HomeScreen(
        uiState = uiState,
        windowSizeClass = windowSizeClass,
        onIntent = viewModel::onIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    windowSizeClass: WindowSizeClass,
    onIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontWeight = FontWeight.Bold,
                    )
                },
                actions = {
                    IconButton(onClick = { onIntent(HomeIntent.OnDownloadsClicked) }) {
                        Icon(
                            imageVector = Icons.Filled.Download,
                            contentDescription = stringResource(R.string.home_downloads_icon_description),
                        )
                    }
                    IconButton(onClick = { onIntent(HomeIntent.OnShortsClicked) }) {
                        Icon(
                            imageVector = Icons.Filled.PlayCircle,
                            contentDescription = stringResource(R.string.home_shorts_icon_description),
                        )
                    }

                    IconButton(onClick = { onIntent(HomeIntent.OnProfileClicked) }) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = stringResource(R.string.home_profile_icon_description),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { innerPadding ->
        when {
            uiState.isLoading && uiState.videos.isEmpty() -> CircularCommonLoader(
                modifier = Modifier.padding(innerPadding),
            )
            uiState.errorMessage != null && uiState.videos.isEmpty() -> ErrorContent(
                modifier = Modifier.padding(innerPadding),
                onRetryClicked = { onIntent(HomeIntent.OnRetryClicked) },
            )
            else -> HomeFeedContent(
                modifier = Modifier.padding(innerPadding),
                videos = uiState.videos,
                windowSizeClass = windowSizeClass,
                onVideoClicked = { onIntent(HomeIntent.OnVideoThumbnailClicked(it)) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(name = "Mobile", device = Devices.PHONE)
@Composable
private fun HomeScreenMobilePreview() {
    StreamlyTheme {
        HomeScreen(
            uiState = HomeUiState(videos = sampleVideos),
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
            onIntent = {},
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(name = "Foldable", device = Devices.FOLDABLE)
@Composable
private fun HomeScreenFoldablePreview() {
    StreamlyTheme {
        HomeScreen(
            uiState = HomeUiState(videos = sampleVideos),
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(673.dp, 841.dp)),
            onIntent = {},
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(name = "Tablet", device = Devices.TABLET)
@Composable
private fun HomeScreenTabletPreview() {
    StreamlyTheme {
        HomeScreen(
            uiState = HomeUiState(videos = sampleVideos),
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(1280.dp, 800.dp)),
            onIntent = {},
        )
    }
}

private val sampleVideos = listOf(
    VideoUiModel(
        id = "1",
        title = "Jetpack Compose",
        channel = "Android Academy",
        thumbnailUrl = "https://picsum.photos/seed/video1/640/360",
        channelAvatarUrl = "https://i.pravatar.cc/150?img=1",
        views = "1.2M views",
        uploadedAt = "2 weeks ago",
        videoUrl = "",
    ),
    VideoUiModel(
        id = "2",
        title = "Media3 Player Tutorial",
        channel = "Compose Hub",
        thumbnailUrl = "https://picsum.photos/seed/video2/640/360",
        channelAvatarUrl = "https://i.pravatar.cc/150?img=2",
        views = "845K views",
        uploadedAt = "5 days ago",
        videoUrl = "",
    ),
)