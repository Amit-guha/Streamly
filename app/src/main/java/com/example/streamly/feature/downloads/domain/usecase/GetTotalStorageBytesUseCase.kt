package com.example.streamly.feature.downloads.domain.usecase

import com.example.streamly.feature.downloads.domain.repository.DownloadsRepository
import javax.inject.Inject

class GetTotalStorageBytesUseCase @Inject constructor(
    private val downloadsRepository: DownloadsRepository,
) {
    suspend operator fun invoke(): Long = downloadsRepository.getTotalStorageBytes()
}
