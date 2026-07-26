package com.example.streamly.feature.home.domain.model

data class Video(
    val id: String,
    val title: String,
    val channel: String,
    val thumbnailUrl: String,
    val channelAvatarUrl: String,
    val views: String,
    val uploadedAt: String,
    val videoUrl: String,
)