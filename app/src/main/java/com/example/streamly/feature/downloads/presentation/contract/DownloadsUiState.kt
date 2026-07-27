package com.example.streamly.feature.downloads.presentation.contract

import com.example.streamly.feature.downloads.domain.model.DownloadItem

data class DownloadsUiState(
    val downloads: List<DownloadItem> = emptyList(),
    val usedStorageBytes: Long = 0L,
    val totalStorageBytes: Long = 0L,
)