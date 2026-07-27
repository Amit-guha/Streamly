package com.example.streamly.feature.downloads.domain.usecase

import com.example.streamly.feature.downloads.domain.repository.DownloadsRepository
import javax.inject.Inject

class DownloadVideoUseCase @Inject constructor(
    private val downloadsRepository: DownloadsRepository,
) {
    suspend operator fun invoke(videoId: String, title: String, thumbnailUrl: String?, videoUrl: String) =
        downloadsRepository.startDownload(videoId, title, thumbnailUrl, videoUrl)
}