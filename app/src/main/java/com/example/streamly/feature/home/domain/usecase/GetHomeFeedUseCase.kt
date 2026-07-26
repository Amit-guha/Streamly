package com.example.streamly.feature.home.domain.usecase

import com.example.streamly.core.common.util.Result
import com.example.streamly.feature.home.domain.model.Video
import com.example.streamly.feature.home.domain.repository.HomeRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetHomeFeedUseCase @Inject constructor(
    private val homeRepository: HomeRepository,
) {
    operator fun invoke(): Flow<Result<List<Video>>> = homeRepository.getVideos()
}