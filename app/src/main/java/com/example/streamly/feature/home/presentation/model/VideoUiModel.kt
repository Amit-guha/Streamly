package com.example.streamly.feature.home.presentation.model

import com.example.streamly.feature.home.domain.model.Video

data class VideoUiModel(
    val id: String,
    val title: String,
    val channel: String,
    val thumbnailUrl: String,
    val channelAvatarUrl: String,
    val views: String,
    val uploadedAt: String,
    val videoUrl: String,
)

fun Video.toUiModel(): VideoUiModel = VideoUiModel(
    id = id,
    title = title,
    channel = channel,
    thumbnailUrl = thumbnailUrl,
    channelAvatarUrl = channelAvatarUrl,
    views = views,
    uploadedAt = uploadedAt,
    videoUrl = videoUrl,
)