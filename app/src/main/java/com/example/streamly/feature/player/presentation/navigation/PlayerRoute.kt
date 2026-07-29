package com.example.streamly.feature.player.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.example.streamly.core.navigation.NavigationDestination
import com.example.streamly.feature.player.presentation.PlayerScreenRoute
import kotlinx.serialization.Serializable

@Serializable
data class PlayerNavKey(
    val videoId: String,
    val title: String,
    val videoUrl: String,
) : NavigationDestination

fun EntryProviderScope<NavigationDestination>.playerEntries(
    onBack: () -> Unit,
    onNavigate: (NavigationDestination) -> Unit,
    onNavigateToDownloadsAfterDelete: () -> Unit,
) {
    entry<PlayerNavKey> { key ->
        PlayerScreenRoute(
            videoId = key.videoId,
            title = key.title,
            videoUrl = key.videoUrl,
            onBack = onBack,
            onNavigate = onNavigate,
            onNavigateToDownloadsAfterDelete = onNavigateToDownloadsAfterDelete,
        )
    }
}