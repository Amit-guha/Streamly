package com.example.streamly.feature.player.data.repository

import com.example.streamly.core.common.enum.Status
import com.example.streamly.core.common.util.Result
import com.example.streamly.core.common.util.resultFlow
import com.example.streamly.feature.home.domain.repository.HomeRepository
import com.example.streamly.feature.player.domain.model.PlayerVideoDetails
import com.example.streamly.feature.player.domain.repository.PlayerRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first


class PlayerRepositoryImpl @Inject constructor(
    private val homeRepository: HomeRepository,
) : PlayerRepository {

    override fun getVideoDetails(videoId: String): Flow<Result<PlayerVideoDetails>> = resultFlow {
        val videos = homeRepository.getVideos().first { it.status != Status.LOADING }.data.orEmpty()
        val video = videos.first { it.id == videoId }
        val upNext = videos.filterNot { it.id == videoId }
        PlayerVideoDetails(video = video, upNext = upNext)
    }
}