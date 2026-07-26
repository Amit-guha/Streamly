package com.example.streamly.feature.home.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class VideoDto(
    val id: String,
    val title: String,
    val channel: String,
    val thumbnail: String,
    val channelAvatar: String,
    val duration: String,
    val views: String,
    val uploadedAt: String,
    val subscribers: String,
    val likes: String,
    val description: String,
    val videoUrl: String,
)