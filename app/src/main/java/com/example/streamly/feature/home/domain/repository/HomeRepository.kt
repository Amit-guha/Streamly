package com.example.streamly.feature.home.domain.repository

import com.example.streamly.core.common.util.Result
import com.example.streamly.feature.home.domain.model.Video
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getVideos(): Flow<Result<List<Video>>>
}