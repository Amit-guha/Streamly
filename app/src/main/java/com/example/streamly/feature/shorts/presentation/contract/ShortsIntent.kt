package com.example.streamly.feature.shorts.presentation.contract

sealed interface ShortsIntent {
    data object OnScreenStarted : ShortsIntent
    data class OnPageChanged(val index: Int) : ShortsIntent
    data object OnMuteToggled : ShortsIntent
    data object OnRetryClicked : ShortsIntent
    data object OnLifecyclePaused : ShortsIntent
    data object OnLifecycleResumed : ShortsIntent
    data object OnBackRequested : ShortsIntent
}