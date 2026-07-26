package com.example.streamly.feature.player.domain.usecase

import com.example.streamly.core.common.util.Result
import com.example.streamly.feature.player.domain.model.PlayerVideoDetails
import com.example.streamly.feature.player.domain.repository.PlayerRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetVideoDetailsUseCase @Inject constructor(
    private val playerRepository: PlayerRepository,
) {
    operator fun invoke(videoId: String): Flow<Result<PlayerVideoDetails>> = playerRepository.getVideoDetails(videoId)
}