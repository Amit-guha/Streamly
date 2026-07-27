package com.example.streamly.feature.shorts.domain.repository

import com.example.streamly.core.common.util.Result
import com.example.streamly.feature.shorts.domain.model.Short
import kotlinx.coroutines.flow.Flow

interface ShortsRepository {
    fun getShorts(): Flow<Result<List<Short>>>
}