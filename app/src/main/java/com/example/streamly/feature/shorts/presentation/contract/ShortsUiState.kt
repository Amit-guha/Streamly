package com.example.streamly.feature.shorts.presentation.contract

import com.example.streamly.feature.shorts.presentation.model.ShortUiModel

data class ShortsUiState(
    val isLoading: Boolean = false,
    val shorts: List<ShortUiModel> = emptyList(),
    val currentIndex: Int = 0,
    // Bumped every time the pool prepares a player, even when currentIndex is unchanged (e.g.
    // the very first page). StateFlow conflates structurally-equal updates, so without this the
    // page-0 player pool.prepare() creates would never trigger the recomposition needed to read
    // it via ShortsViewModel.playerFor.
    val playerGeneration: Int = 0,
    val isMuted: Boolean = false,
    val errorMessage: String? = null,
)