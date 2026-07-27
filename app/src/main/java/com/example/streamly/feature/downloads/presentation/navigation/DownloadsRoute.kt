package com.example.streamly.feature.downloads.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.example.streamly.core.navigation.NavigationDestination
import com.example.streamly.feature.downloads.presentation.DownloadsScreenRoute
import com.example.streamly.feature.player.presentation.navigation.PlayerNavKey
import kotlinx.serialization.Serializable

@Serializable
data object DownloadsNavKey : NavigationDestination

fun EntryProviderScope<NavigationDestination>.downloadsEntries(
    onBack: () -> Unit,
    onNavigate: (NavigationDestination) -> Unit,
) {
    entry<DownloadsNavKey> {
        DownloadsScreenRoute(
            onBack = onBack,
            onNavigateToPlayer = { videoId, title, videoUrl ->
                onNavigate(PlayerNavKey(videoId = videoId, title = title, videoUrl = videoUrl))
            },
        )
    }
}