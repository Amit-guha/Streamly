package com.example.streamly.feature.shorts.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.example.streamly.core.navigation.NavigationDestination
import com.example.streamly.feature.shorts.presentation.ShortsScreenRoute
import kotlinx.serialization.Serializable

@Serializable
data object ShortsNavKey : NavigationDestination

fun EntryProviderScope<NavigationDestination>.shortsEntries(
    onBack: () -> Unit,
) {
    entry<ShortsNavKey> {
        ShortsScreenRoute(onBack = onBack)
    }
}