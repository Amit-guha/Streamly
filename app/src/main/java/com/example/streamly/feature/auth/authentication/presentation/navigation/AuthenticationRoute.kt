package com.example.streamly.feature.auth.authentication.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.example.streamly.core.navigation.NavigationDestination
import com.example.streamly.feature.auth.authentication.presentation.AuthenticationScreenRoute
import kotlinx.serialization.Serializable

@Serializable
data object AuthenticationNavKey : NavigationDestination

fun EntryProviderScope<NavigationDestination>.authenticationEntries(
    onNavigate: (NavigationDestination) -> Unit,
    onAuthenticated: () -> Unit,
) {
    entry<AuthenticationNavKey> {
        AuthenticationScreenRoute(onNavigate = onNavigate, onAuthenticated = onAuthenticated)
    }
}