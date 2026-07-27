package com.example.streamly.feature.player.presentation.contract

import com.example.streamly.core.common.enum.DownloadStatus
import com.example.streamly.feature.player.presentation.model.VideoUiModel

data class PlayerUiState(
    val video: VideoUiModel = VideoUiModel(
        id = "",
        title = "",
        channel = "",
        thumbnailUrl = "",
        channelAvatarUrl = "",
        views = "",
        uploadedAt = "",
        videoUrl = "",
    ),
    val isSubscribed: Boolean = false,
    val isLiked: Boolean = false,
    val isMuted: Boolean = false,
    val upNext: List<VideoUiModel> = emptyList(),
    val isLoadingUpNext: Boolean = false,
    val upNextErrorMessage: String? = null,
    val downloadStatus: DownloadStatus? = null,
    val showDownloadOptionsSheet: Boolean = false,
)