package com.example.streamly.feature.home.presentation.contract

import com.example.streamly.feature.home.presentation.model.VideoUiModel

sealed interface HomeEffect {
    data class NavigateToPlayer(val video: VideoUiModel) : HomeEffect
    data object NavigateToShorts : HomeEffect
    data object NavigateToDownloads : HomeEffect
    data object NavigateToProfile : HomeEffect
}