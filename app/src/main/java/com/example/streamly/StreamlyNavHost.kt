package com.example.streamly

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import com.example.streamly.core.navigation.AppNavGraph
import com.example.streamly.core.navigation.NavigationDestination
import com.example.streamly.core.navigation.rememberAppNavigator
import com.example.streamly.feature.auth.authentication.presentation.navigation.AuthenticationNavKey
import com.example.streamly.feature.auth.authentication.presentation.navigation.authenticationEntries
import com.example.streamly.feature.auth.signinwithemail.presentation.navigation.signInWithEmailEntries
import com.example.streamly.feature.downloads.presentation.navigation.DownloadsNavKey
import com.example.streamly.feature.downloads.presentation.navigation.downloadsEntries
import com.example.streamly.feature.home.presentation.navigation.HomeNavKey
import com.example.streamly.feature.home.presentation.navigation.homeEntries
import com.example.streamly.feature.player.presentation.navigation.playerEntries
import com.example.streamly.feature.profile.presentation.navigation.profileEntries
import com.example.streamly.feature.shorts.presentation.navigation.shortsEntries
import com.example.streamly.feature.splash.presentation.navigation.SplashNavKey
import com.example.streamly.feature.splash.presentation.navigation.splashEntries

/**
 * Central navigation host. Every feature owns its own `presentation/navigation` package
 * (a NavKey + an [androidx.navigation3.runtime.EntryProviderScope] extension); this is the
 * one place that aggregates them into a single back stack.
 *
 * Always starts at [SplashNavKey] — Splash is the one that decides (via session state) whether
 * to land on Authentication or Home, and replaces itself in the back stack once it does, so it
 * never lingers as a back destination.
 */
@Composable
fun StreamlyNavHost() {
    val navigator = rememberAppNavigator(startDestination = SplashNavKey)

    AppNavGraph(
        backStack = navigator.backStack,
        onBack = navigator::navigateBack,
        entryProvider = entryProvider<NavigationDestination> {
            splashEntries(onNavigate = navigator::replaceBackStackWith)
            authenticationEntries(
                onNavigate = navigator::navigateTo,
                onAuthenticated = { navigator.replaceBackStackWith(HomeNavKey) },
            )
            signInWithEmailEntries(
                onAuthenticated = { navigator.replaceBackStackWith(HomeNavKey) },
            )
            homeEntries(onNavigate = navigator::navigateTo)
            downloadsEntries(onBack = navigator::navigateBack, onNavigate = navigator::navigateTo)
            playerEntries(onBack = navigator::navigateBack, onNavigate = navigator::navigateTo)
            profileEntries(
                onBack = navigator::navigateBack,
                onNavigateToDownloads = { navigator.navigateTo(DownloadsNavKey) },
                onSignedOut = { navigator.replaceBackStackWith(AuthenticationNavKey) },
            )
            shortsEntries(onBack = navigator::navigateBack)
        },
    )
}