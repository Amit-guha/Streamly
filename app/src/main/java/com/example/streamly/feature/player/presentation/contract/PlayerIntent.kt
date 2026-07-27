package com.example.streamly.feature.player.presentation.contract

import com.example.streamly.feature.player.presentation.model.VideoUiModel

sealed interface PlayerIntent {
    data class OnScreenStarted(val videoId: String, val title: String, val videoUrl: String) : PlayerIntent
    data object OnSubscribeClicked : PlayerIntent
    data object OnLikeClicked : PlayerIntent
    data object OnShareClicked : PlayerIntent
    data object OnDownloadClicked : PlayerIntent
    data object OnRemoveDownloadClicked : PlayerIntent
    data object OnDownloadSheetDismissed : PlayerIntent
    data object OnMuteToggled : PlayerIntent
    data class OnUpNextVideoClicked(val video: VideoUiModel) : PlayerIntent
    data object OnRetryClicked : PlayerIntent
    data object OnLifecyclePaused : PlayerIntent
    data object OnLifecycleResumed : PlayerIntent
    data object OnBackRequested : PlayerIntent
}