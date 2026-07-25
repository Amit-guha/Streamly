package com.example.streamly

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import com.example.streamly.core.navigation.AppNavGraph
import com.example.streamly.core.navigation.NavigationDestination
import com.example.streamly.core.navigation.rememberAppNavigator
import com.example.streamly.feature.home.presentation.navigation.HomeNavKey
import com.example.streamly.feature.home.presentation.navigation.homeEntries
import com.example.streamly.feature.profile.presentation.navigation.profileEntries

/**
 * Central navigation host. Every feature owns its own `presentation/navigation` package
 * (a NavKey + an [androidx.navigation3.runtime.EntryProviderScope] extension); this is the
 * one place that aggregates them into a single back stack.
 */
@Composable
fun StreamlyNavHost() {
    val navigator = rememberAppNavigator(startDestination = HomeNavKey)

    AppNavGraph(
        backStack = navigator.backStack,
        onBack = navigator::navigateBack,
        entryProvider = entryProvider<NavigationDestination> {
            homeEntries(onNavigate = navigator::navigateTo)
            profileEntries(onBack = navigator::navigateBack)
        },
    )
}