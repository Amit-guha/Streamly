package com.example.streamly.feature.home.data.repository

import com.example.streamly.core.common.util.Result
import com.example.streamly.core.common.util.resultFlow
import com.example.streamly.feature.home.data.datasource.remote.HomeRemoteDataSource
import com.example.streamly.feature.home.data.mapper.toDomain
import com.example.streamly.feature.home.domain.model.Video
import com.example.streamly.feature.home.domain.repository.HomeRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class HomeRepositoryImpl @Inject constructor(
    private val remoteDataSource: HomeRemoteDataSource,
) : HomeRepository {
    override fun getVideos(): Flow<Result<List<Video>>> = resultFlow {
        remoteDataSource.getVideos().map { it.toDomain() }
    }
}