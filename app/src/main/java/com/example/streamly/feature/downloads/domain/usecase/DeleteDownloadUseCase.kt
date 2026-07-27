package com.example.streamly.feature.downloads.domain.usecase

import com.example.streamly.feature.downloads.domain.repository.DownloadsRepository
import javax.inject.Inject

class DeleteDownloadUseCase @Inject constructor(
    private val downloadsRepository: DownloadsRepository,
) {
    suspend operator fun invoke(videoId: String) = downloadsRepository.removeDownload(videoId)
}