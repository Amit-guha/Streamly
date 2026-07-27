package com.example.streamly.feature.shorts.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ShortDto(
    val id: String,
    val handle: String,
    val caption: String,
    val description: String,
    val videoUrl: String,
    val thumbnailUrl: String,
)