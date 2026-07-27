package com.example.streamly.feature.shorts.domain.usecase

import com.example.streamly.core.common.util.Result
import com.example.streamly.feature.shorts.domain.model.Short
import com.example.streamly.feature.shorts.domain.repository.ShortsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetShortsUseCase @Inject constructor(
    private val shortsRepository: ShortsRepository,
) {
    operator fun invoke(): Flow<Result<List<Short>>> = shortsRepository.getShorts()
}