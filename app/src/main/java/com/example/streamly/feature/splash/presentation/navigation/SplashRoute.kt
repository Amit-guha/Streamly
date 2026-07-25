package com.example.streamly.feature.splash.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.example.streamly.core.navigation.NavigationDestination
import com.example.streamly.feature.splash.presentation.SplashScreenRoute
import kotlinx.serialization.Serializable

@Serializable
data object SplashNavKey : NavigationDestination

fun EntryProviderScope<NavigationDestination>.splashEntries(
    onNavigate: (NavigationDestination) -> Unit,
) {
    entry<SplashNavKey> {
        SplashScreenRoute(onNavigate = onNavigate)
    }
}