package com.example.streamly.feature.shorts.domain.model

data class Short(
    val id: String,
    val handle: String,
    val caption: String,
    val description: String,
    val videoUrl: String,
    val thumbnailUrl: String,
)