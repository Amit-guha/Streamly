package com.example.streamly.feature.downloads.domain.usecase

import com.example.streamly.feature.downloads.domain.model.DownloadItem
import com.example.streamly.feature.downloads.domain.repository.DownloadsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetDownloadStatusUseCase @Inject constructor(
    private val downloadsRepository: DownloadsRepository,
) {
    operator fun invoke(videoId: String): Flow<DownloadItem?> = downloadsRepository.observeDownload(videoId)
}