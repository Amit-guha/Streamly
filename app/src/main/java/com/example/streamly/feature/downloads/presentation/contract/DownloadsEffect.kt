package com.example.streamly.feature.downloads.presentation.contract

sealed interface DownloadsEffect {
    data object NavigateBack : DownloadsEffect
    data class NavigateToPlayer(val videoId: String, val title: String, val videoUrl: String) : DownloadsEffect
}