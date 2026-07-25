package com.example.streamly.feature.home.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.example.streamly.core.navigation.NavigationDestination
import com.example.streamly.feature.home.presentation.HomeScreenRoute
import kotlinx.serialization.Serializable

@Serializable
data object HomeNavKey : NavigationDestination

fun EntryProviderScope<NavigationDestination>.homeEntries(
    onNavigate: (NavigationDestination) -> Unit,
) {
    entry<HomeNavKey> {
        HomeScreenRoute(onNavigate = onNavigate)
    }
}