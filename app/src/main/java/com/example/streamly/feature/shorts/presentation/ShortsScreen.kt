package com.example.streamly.feature.shorts.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.example.streamly.core.common.constant.AppConstants
import com.example.streamly.core.designsystem.LocalWindowSizeClass
import com.example.streamly.core.designsystem.component.CircularCommonLoader
import com.example.streamly.feature.shorts.presentation.component.ShortPagerItem
import com.example.streamly.feature.shorts.presentation.component.ShortsErrorContent
import com.example.streamly.feature.shorts.presentation.contract.ShortsIntent
import com.example.streamly.feature.shorts.presentation.contract.ShortsUiState
import com.example.streamly.feature.shorts.presentation.model.ShortUiModel
import com.example.streamly.ui.theme.StreamlyTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
internal fun ShortsScreenRoute(
    onBack: () -> Unit,
    viewModel: ShortsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val windowSizeClass = LocalWindowSizeClass.current

    BackHandler {
        viewModel.onIntent(ShortsIntent.OnBackRequested)
        onBack()
    }

    LaunchedEffect(Unit) {
        viewModel.onIntent(ShortsIntent.OnScreenStarted)
    }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> viewModel.onIntent(ShortsIntent.OnLifecyclePaused)
                Lifecycle.Event.ON_RESUME -> viewModel.onIntent(ShortsIntent.OnLifecycleResumed)
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    ShortsScreen(
        uiState = uiState,
        windowSizeClass = windowSizeClass,
        playerFor = viewModel::playerFor,
        onIntent = viewModel::onIntent,
        onBack = {
            viewModel.onIntent(ShortsIntent.OnBackRequested)
            onBack()
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun ShortsScreen(
    uiState: ShortsUiState,
    windowSizeClass: WindowSizeClass,
    playerFor: (Int) -> Player?,
    onIntent: (ShortsIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize(), color = Color.Black) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading && uiState.shorts.isEmpty() -> CircularCommonLoader()
                uiState.errorMessage != null && uiState.shorts.isEmpty() -> ShortsErrorContent(
                    onRetryClicked = { onIntent(ShortsIntent.OnRetryClicked) },
                )
                else -> {
                    val pagerState = rememberPagerState(pageCount = { uiState.shorts.size })

                    LaunchedEffect(pagerState) {
                        snapshotFlow { pagerState.currentPage }.collectLatest { page ->
                            onIntent(ShortsIntent.OnPageChanged(page))
                        }
                    }

                    VerticalPager(
                        state = pagerState,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxHeight()
                            .fillMaxWidth()
                            .widthIn(max = AppConstants.SHORTS_MAX_PAGER_WIDTH_DP.dp),
                    ) { page ->
                        val short = uiState.shorts[page]
                        ShortPagerItem(
                            short = short,
                            player = playerFor(page),
                            isMuted = uiState.isMuted,
                            windowSizeClass = windowSizeClass,
                            onMuteToggled = { onIntent(ShortsIntent.OnMuteToggled) },
                        )
                    }
                }
            }

            ShortsTopBar(onBack = onBack, modifier = Modifier.align(Alignment.TopStart))
        }
    }
}

@Composable
private fun ShortsTopBar(onBack: () -> Unit, modifier: Modifier = Modifier) {
    // safeDrawing (not just statusBars) so a side cutout in landscape doesn't clip the back
    // button/title either - Shorts is full-bleed on every edge, same reasoning as ShortPagerItem.
    Box(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(4.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.shorts_back),
                    tint = Color.White,
                )
            }
            Text(
                text = stringResource(R.string.shorts_title),
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

private val sampleShorts = listOf(
    ShortUiModel(
        id = "s1",
        handle = "@compose_hub",
        caption = "Quick tip #12",
        description = "Autoplay on visible item only",
        videoUrl = "",
        thumbnailUrl = "https://picsum.photos/seed/short1/720/1280",
    ),
    ShortUiModel(
        id = "s2",
        handle = "@android_academy",
        caption = "Media3 pooled players",
        description = "Only the visible short decodes at a time",
        videoUrl = "",
        thumbnailUrl = "https://picsum.photos/seed/short2/720/1280",
    ),
)

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(name = "Mobile", device = Devices.PHONE)
@Composable
private fun ShortsScreenMobilePreview() {
    StreamlyTheme {
        ShortsScreen(
            uiState = ShortsUiState(shorts = sampleShorts),
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
            playerFor = { null },
            onIntent = {},
            onBack = {},
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(name = "Mobile landscape", widthDp = 891, heightDp = 411)
@Composable
private fun ShortsScreenMobileLandscapePreview() {
    StreamlyTheme {
        ShortsScreen(
            uiState = ShortsUiState(shorts = sampleShorts),
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(891.dp, 411.dp)),
            playerFor = { null },
            onIntent = {},
            onBack = {},
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(name = "Foldable", device = Devices.FOLDABLE)
@Composable
private fun ShortsScreenFoldablePreview() {
    StreamlyTheme {
        ShortsScreen(
            uiState = ShortsUiState(shorts = sampleShorts),
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(673.dp, 841.dp)),
            playerFor = { null },
            onIntent = {},
            onBack = {},
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(name = "Tablet", device = Devices.TABLET)
@Composable
private fun ShortsScreenTabletPreview() {
    StreamlyTheme {
        ShortsScreen(
            uiState = ShortsUiState(shorts = sampleShorts),
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(1280.dp, 800.dp)),
            playerFor = { null },
            onIntent = {},
            onBack = {},
        )
    }
}