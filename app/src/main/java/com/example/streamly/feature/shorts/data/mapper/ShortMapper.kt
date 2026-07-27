package com.example.streamly.feature.shorts.data.mapper

import com.example.streamly.feature.shorts.data.dto.ShortDto
import com.example.streamly.feature.shorts.domain.model.Short

fun ShortDto.toDomain(): Short = Short(
    id = id,
    handle = handle,
    caption = caption,
    description = description,
    videoUrl = videoUrl,
    thumbnailUrl = thumbnailUrl,
)