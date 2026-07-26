package com.example.streamly.feature.home.presentation.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.streamly.feature.home.presentation.model.VideoUiModel

private val VIDEO_CARD_MIN_WIDTH = 240.dp

/**
 * Phones (compact width) get the classic single-column feed. Foldables and tablets (medium or
 * expanded width) switch to a grid that fits as many [VIDEO_CARD_MIN_WIDTH]-wide columns as the
 * available width allows — no hardcoded column count, same idea as YouTube's tablet grid.
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
internal fun HomeFeedContent(
    videos: List<VideoUiModel>,
    windowSizeClass: WindowSizeClass,
    onVideoClicked: (VideoUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            item { CategoryChipsRow() }

            items(videos, key = { it.id }) { video ->
                VideoCard(
                    video = video,
                    onClick = { onVideoClicked(video) },
                )
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = VIDEO_CARD_MIN_WIDTH),
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) { CategoryChipsRow() }

            items(videos, key = { it.id }) { video ->
                VideoCard(
                    video = video,
                    onClick = { onVideoClicked(video) },
                )
            }
        }
    }
}