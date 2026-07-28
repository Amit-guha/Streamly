package com.example.streamly.feature.shorts.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import androidx.media3.ui.compose.state.rememberPresentationState
import com.example.streamly.core.designsystem.component.CircularCommonLoader
import com.example.streamly.feature.shorts.presentation.model.ShortUiModel
import com.example.streamly.ui.theme.StreamlyTheme
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay

private val CONTROLS_AUTO_HIDE_DURATION = 3.seconds

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@androidx.annotation.OptIn(markerClass = [UnstableApi::class])
@Composable
internal fun ShortPagerItem(
    short: ShortUiModel,
    player: Player?,
    isMuted: Boolean,
    windowSizeClass: WindowSizeClass,
    onMuteToggled: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val isCompactHeight = windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
        ) {
            if (player != null) {
                val presentationState = rememberPresentationState(player)
                val isBuffering = rememberIsBuffering(player)

                PlayerSurface(
                    player = player,
                    modifier = Modifier
                        .fillMaxSize()
                        .resizeWithContentScale(ContentScale.Crop, presentationState.videoSizeDp),
                )

                if (presentationState.coverSurface) {
                    ShortThumbnail(short)
                }

                var controlsVisible by remember { mutableStateOf(false) }
                var lastInteractionAt by remember { mutableIntStateOf(0) }

                LaunchedEffect(controlsVisible, lastInteractionAt, player.playWhenReady) {
                    if (controlsVisible && player.playWhenReady) {
                        delay(CONTROLS_AUTO_HIDE_DURATION)
                        controlsVisible = false
                    }
                }

                val playPauseInteractionSource = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = playPauseInteractionSource,
                            indication = null,
                            onClick = {
                                player.playWhenReady = !player.playWhenReady
                                controlsVisible = true
                                lastInteractionAt++
                            },
                        ),
                )

                if (presentationState.coverSurface || isBuffering) {
                    CircularCommonLoader(indicatorColor = Color.White, isBlockingInteraction = false)
                } else {
                    AnimatedVisibility(
                        visible = controlsVisible,
                        enter = fadeIn(),
                        exit = fadeOut(),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .pointerInput(Unit) {
                                awaitEachGesture {
                                    awaitFirstDown(requireUnconsumed = false)
                                    lastInteractionAt++
                                }
                            },
                    ) {
                        ShortPlayerControls(player = player)
                    }
                }
            } else {
                ShortThumbnail(short)
            }

            if (isCompactHeight) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        if (player != null) {
                            ShortProgressBar(player = player, modifier = Modifier.fillMaxWidth())
                        }
                        ShortCaptionOverlay(short = short, modifier = Modifier.padding(top = 4.dp))
                    }
                    ShortActionRail(
                        isMuted = isMuted,
                        onMuteToggled = onMuteToggled,
                        modifier = Modifier.padding(start = 12.dp),
                    )
                }
            } else {
                ShortActionRail(
                    isMuted = isMuted,
                    onMuteToggled = onMuteToggled,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp),
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    if (player != null) {
                        ShortProgressBar(player = player, modifier = Modifier.fillMaxWidth())
                    }
                    ShortCaptionOverlay(short = short, modifier = Modifier.padding(top = 4.dp, end = 64.dp))
                }
            }
        }
    }
}

// player is null in preview (ExoPlayer can't run in the preview renderer) — same convention as
// PlayerSurfacePreview in the player feature — so this exercises the thumbnail-cover fallback.
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(name = "Mobile", device = Devices.PHONE)
@Composable
private fun ShortPagerItemPreview() {
    StreamlyTheme {
        ShortPagerItem(
            short = ShortUiModel(
                id = "s1",
                handle = "@compose_hub",
                caption = "Quick tip #12",
                description = "Autoplay on visible item only",
                videoUrl = "",
                thumbnailUrl = "https://picsum.photos/seed/short1/720/1280",
            ),
            player = null,
            isMuted = false,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
            onMuteToggled = {},
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(name = "Mobile landscape", widthDp = 891, heightDp = 411)
@Composable
private fun ShortPagerItemLandscapePreview() {
    StreamlyTheme {
        ShortPagerItem(
            short = ShortUiModel(
                id = "s1",
                handle = "@compose_hub",
                caption = "Quick tip #12",
                description = "Autoplay on visible item only",
                videoUrl = "",
                thumbnailUrl = "https://picsum.photos/seed/short1/720/1280",
            ),
            player = null,
            isMuted = false,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(891.dp, 411.dp)),
            onMuteToggled = {},
        )
    }
}