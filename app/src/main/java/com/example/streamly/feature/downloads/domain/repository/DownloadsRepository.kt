package com.example.streamly.feature.downloads.domain.repository

import com.example.streamly.feature.downloads.domain.model.DownloadItem
import kotlinx.coroutines.flow.Flow

interface DownloadsRepository {
    fun observeDownloads(): Flow<List<DownloadItem>>
    fun observeDownload(videoId: String): Flow<DownloadItem?>
    suspend fun startDownload(videoId: String, title: String, thumbnailUrl: String?, videoUrl: String)
    suspend fun removeDownload(videoId: String)
    suspend fun getTotalStorageBytes(): Long
}