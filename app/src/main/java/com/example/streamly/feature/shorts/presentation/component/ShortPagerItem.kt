package com.example.streamly.feature.shorts.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import androidx.media3.ui.compose.state.rememberPresentationState
import coil.compose.AsyncImage
import com.example.streamly.R
import com.example.streamly.feature.shorts.presentation.model.ShortUiModel
import com.example.streamly.ui.theme.StreamlyTheme

@androidx.annotation.OptIn(markerClass = [UnstableApi::class])
@Composable
internal fun ShortPagerItem(
    short: ShortUiModel,
    player: Player?,
    isMuted: Boolean,
    onMuteToggled: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        if (player != null) {
            val presentationState = rememberPresentationState(player)

            PlayerSurface(
                player = player,
                modifier = Modifier
                    .fillMaxSize()
                    .resizeWithContentScale(ContentScale.Crop, presentationState.videoSizeDp),
            )

            if (presentationState.coverSurface) {
                ShortThumbnail(short)
            }

            val playPauseInteractionSource = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = playPauseInteractionSource,
                        indication = null,
                        onClick = { player.playWhenReady = !player.playWhenReady },
                    ),
            )

            ShortPlayerControls(player = player, modifier = Modifier.align(Alignment.Center))
        } else {
            ShortThumbnail(short)
        }

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
                .windowInsetsPadding(WindowInsets.navigationBars)
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

@Composable
private fun ShortThumbnail(short: ShortUiModel, modifier: Modifier = Modifier) {
    AsyncImage(
        model = short.thumbnailUrl,
        contentDescription = stringResource(R.string.shorts_thumbnail_description),
        modifier = modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
    )
}

// player is null in preview (ExoPlayer can't run in the preview renderer) — same convention as
// PlayerSurfacePreview in the player feature — so this exercises the thumbnail-cover fallback.
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
            onMuteToggled = {},
        )
    }
}