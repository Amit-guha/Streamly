package com.example.streamly.feature.downloads.presentation.contract

import com.example.streamly.feature.downloads.domain.model.DownloadItem

sealed interface DownloadsIntent {
    data object OnScreenStarted : DownloadsIntent
    data class OnItemClicked(val download: DownloadItem) : DownloadsIntent
    data class OnRemoveClicked(val videoId: String) : DownloadsIntent
    data object OnBackClicked : DownloadsIntent
}