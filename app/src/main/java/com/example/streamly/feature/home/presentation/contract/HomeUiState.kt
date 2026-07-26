package com.example.streamly.feature.home.presentation.contract
import com.example.streamly.feature.home.presentation.model.VideoUiModel

data class HomeUiState(
    val isLoading: Boolean = false,
    val videos: List<VideoUiModel> = emptyList(),
    val errorMessage: String? = null,
)