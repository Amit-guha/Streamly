package com.example.streamly.feature.shorts.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.example.streamly.R
import com.example.streamly.feature.shorts.presentation.model.ShortUiModel
import com.example.streamly.ui.theme.StreamlyTheme

/** Static cover image shown before a Short's video has a player (pool exhausted) or before its
 * first frame has rendered ([androidx.media3.ui.compose.state.PresentationState.coverSurface]). */
@Composable
internal fun ShortThumbnail(short: ShortUiModel, modifier: Modifier = Modifier) {
    AsyncImage(
        model = short.thumbnailUrl,
        contentDescription = stringResource(R.string.shorts_thumbnail_description),
        modifier = modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
    )
}

@Preview(name = "Mobile")
@Composable
private fun ShortThumbnailPreview() {
    StreamlyTheme {
        ShortThumbnail(
            short = ShortUiModel(
                id = "s1",
                handle = "@compose_hub",
                caption = "Quick tip #12",
                description = "Autoplay on visible item only",
                videoUrl = "",
                thumbnailUrl = "https://picsum.photos/seed/short1/720/1280",
            ),
            modifier = Modifier.background(Color.Black),
        )
    }
}