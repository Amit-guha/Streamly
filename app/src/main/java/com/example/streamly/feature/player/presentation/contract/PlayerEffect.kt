package com.example.streamly.feature.player.presentation.contract

sealed interface PlayerEffect {
    data class ShareVideo(val videoUrl: String, val title: String) : PlayerEffect
    data object ShowDownloadStartedSnackbar : PlayerEffect
}