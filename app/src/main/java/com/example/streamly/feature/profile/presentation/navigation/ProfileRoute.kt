package com.example.streamly.feature.profile.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.example.streamly.core.navigation.NavigationDestination
import com.example.streamly.feature.profile.presentation.ProfileScreenRoute
import kotlinx.serialization.Serializable

@Serializable
data object ProfileNavKey : NavigationDestination

fun EntryProviderScope<NavigationDestination>.profileEntries(
    onBack: () -> Unit,
) {
    entry<ProfileNavKey> {
        ProfileScreenRoute(onBack = onBack)
    }
}