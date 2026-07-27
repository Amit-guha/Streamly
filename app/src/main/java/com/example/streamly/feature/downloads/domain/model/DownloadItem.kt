package com.example.streamly.feature.downloads.domain.model

import com.example.streamly.core.common.enum.DownloadStatus

data class DownloadItem(
    val videoId: String,
    val title: String,
    val thumbnailUrl: String?,
    val videoUrl: String,
    val status: DownloadStatus,
    val progressPercent: Int,
    val downloadedBytes: Long,
    val totalBytes: Long,
)