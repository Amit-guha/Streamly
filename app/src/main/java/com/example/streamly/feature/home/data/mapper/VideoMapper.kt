package com.example.streamly.feature.home.data.mapper

import com.example.streamly.feature.home.data.dto.VideoDto
import com.example.streamly.feature.home.domain.model.Video

/** Only the fields the home feed (and player) actually use; [VideoDto.duration],
 * [VideoDto.subscribers], [VideoDto.likes], and [VideoDto.description] aren't needed yet. */
fun VideoDto.toDomain(): Video = Video(
    id = id,
    title = title,
    channel = channel,
    thumbnailUrl = thumbnail,
    channelAvatarUrl = channelAvatar,
    views = views,
    uploadedAt = uploadedAt,
    videoUrl = videoUrl,
)