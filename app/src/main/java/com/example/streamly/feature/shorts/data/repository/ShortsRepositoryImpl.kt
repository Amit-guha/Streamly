package com.example.streamly.feature.shorts.data.repository

import com.example.streamly.core.common.util.Result
import com.example.streamly.core.common.util.resultFlow
import com.example.streamly.feature.shorts.data.datasource.remote.ShortsRemoteDataSource
import com.example.streamly.feature.shorts.data.mapper.toDomain
import com.example.streamly.feature.shorts.domain.model.Short
import com.example.streamly.feature.shorts.domain.repository.ShortsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ShortsRepositoryImpl @Inject constructor(
    private val remoteDataSource: ShortsRemoteDataSource,
) : ShortsRepository {
    override fun getShorts(): Flow<Result<List<Short>>> = resultFlow {
        remoteDataSource.getShorts().map { it.toDomain() }
    }
}