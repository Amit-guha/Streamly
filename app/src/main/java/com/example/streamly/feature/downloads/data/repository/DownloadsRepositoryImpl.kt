package com.example.streamly.feature.downloads.data.repository

import androidx.media3.common.util.UnstableApi
import com.example.streamly.feature.downloads.data.datasource.local.DownloadDao
import com.example.streamly.feature.downloads.data.download.DownloadController
import com.example.streamly.feature.downloads.data.download.DownloadRequestFactory
import com.example.streamly.feature.downloads.data.mapper.pendingDownloadEntity
import com.example.streamly.feature.downloads.data.mapper.toDomain
import com.example.streamly.feature.downloads.data.storage.StorageInfoProvider
import com.example.streamly.feature.downloads.domain.model.DownloadItem
import com.example.streamly.feature.downloads.domain.repository.DownloadsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DownloadsRepositoryImpl @Inject constructor(
    private val downloadDao: DownloadDao,
    private val downloadController: DownloadController,
    private val downloadRequestFactory: DownloadRequestFactory,
    private val storageInfoProvider: StorageInfoProvider,
) : DownloadsRepository {

    override fun observeDownloads(): Flow<List<DownloadItem>> =
        downloadDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeDownload(videoId: String): Flow<DownloadItem?> =
        downloadDao.observeByVideoId(videoId).map { it?.toDomain() }

    @androidx.annotation.OptIn(UnstableApi::class)
    override suspend fun startDownload(videoId: String, title: String, thumbnailUrl: String?, videoUrl: String) {
        downloadDao.insert(pendingDownloadEntity(videoId = videoId, title = title, thumbnailUrl = thumbnailUrl))
        val request = downloadRequestFactory.create(videoId = videoId, title = title, videoUrl = videoUrl)
        downloadController.start(request)
    }

    override suspend fun removeDownload(videoId: String) {
        downloadController.remove(videoId)
    }

    override suspend fun clearAllDownloads() {
        downloadDao.observeAll().first().forEach { entity -> downloadController.remove(entity.videoId) }
    }

    override suspend fun getTotalStorageBytes(): Long = storageInfoProvider.totalStorageBytes()
}