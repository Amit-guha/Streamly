package com.example.streamly.feature.player.domain.model

import com.example.streamly.feature.home.domain.model.Video

data class PlayerVideoDetails(
    val video: Video,
    val upNext: List<Video>,
)