package com.example.streamly.feature.player.domain.repository

import com.example.streamly.core.common.util.Result
import com.example.streamly.feature.player.domain.model.PlayerVideoDetails
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
    fun getVideoDetails(videoId: String): Flow<Result<PlayerVideoDetails>>
}