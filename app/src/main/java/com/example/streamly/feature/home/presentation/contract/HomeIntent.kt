package com.example.streamly.feature.home.presentation.contract

import com.example.streamly.feature.home.presentation.model.VideoUiModel

sealed interface HomeIntent {
    data class OnVideoThumbnailClicked(val video: VideoUiModel) : HomeIntent
    data object OnRetryClicked : HomeIntent
    data object OnShortsClicked : HomeIntent
}