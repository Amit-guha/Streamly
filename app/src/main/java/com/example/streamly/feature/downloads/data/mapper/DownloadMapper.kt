package com.example.streamly.feature.downloads.data.mapper

import com.example.streamly.core.common.enum.DownloadStatus
import com.example.streamly.feature.downloads.data.datasource.local.DownloadEntity
import com.example.streamly.feature.downloads.domain.model.DownloadItem

fun DownloadEntity.toDomain(): DownloadItem = DownloadItem(
    videoId = videoId,
    title = title,
    thumbnailUrl = thumbnailUrl,
    videoUrl = localUri.orEmpty(),
    status = runCatching { DownloadStatus.valueOf(status) }.getOrDefault(DownloadStatus.PENDING),
    progressPercent = progressPercent,
    downloadedBytes = downloadedBytes,
    totalBytes = totalBytes,
)


fun pendingDownloadEntity(videoId: String, title: String, thumbnailUrl: String?): DownloadEntity = DownloadEntity(
    videoId = videoId,
    title = title,
    thumbnailUrl = thumbnailUrl,
    localUri = null,
    status = DownloadStatus.PENDING.name,
    progressPercent = 0,
    downloadedBytes = 0L,
    totalBytes = 0L,
    createdAtMillis = System.currentTimeMillis(),
)