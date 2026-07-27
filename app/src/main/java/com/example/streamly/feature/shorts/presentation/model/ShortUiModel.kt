package com.example.streamly.feature.shorts.presentation.model

import com.example.streamly.feature.shorts.domain.model.Short

data class ShortUiModel(
    val id: String,
    val handle: String,
    val caption: String,
    val description: String,
    val videoUrl: String,
    val thumbnailUrl: String,
)

fun Short.toUiModel(): ShortUiModel = ShortUiModel(
    id = id,
    handle = handle,
    caption = caption,
    description = description,
    videoUrl = videoUrl,
    thumbnailUrl = thumbnailUrl,
)