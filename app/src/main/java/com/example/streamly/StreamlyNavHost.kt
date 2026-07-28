package com.example.streamly

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import com.example.streamly.core.navigation.AppNavGraph
import com.example.streamly.core.navigation.NavigationDestination
import com.example.streamly.core.navigation.rememberAppNavigator
import com.example.streamly.feature.auth.authentication.presentation.navigation.AuthenticationNavKey
import com.example.streamly.feature.auth.authentication.presentation.navigation.authenticationEntries
import com.example.streamly.feature.auth.signinwithemail.presentation.navigation.signInWithEmailEntries
import com.example.streamly.feature.downloads.presentation.navigation.downloadsEntries
import com.example.streamly.feature.player.presentation.navigation.playerEntries
import com.example.streamly.feature.splash.presentation.navigation.SplashNavKey
import com.example.streamly.feature.splash.presentation.navigation.splashEntries

/**
 * Central navigation host. Every feature owns its own `presentation/navigation` package
 * (a NavKey + an [androidx.navigation3.runtime.EntryProviderScope] extension); this is the
 * one place that aggregates them into a single back stack.
 *
 * Always starts at [SplashNavKey] — Splash is the one that decides (via session state) whether
 * to land on Authentication or [MainNavKey], and replaces itself in the back stack once it does,
 * so it never lingers as a back destination. [MainNavKey] hosts Home/Shorts/Profile behind a
 * bottom nav bar as local tab state (see [MainScreenRoute]) rather than as separate back-stack
 * entries — only Downloads and Player push onto this back stack on top of it.
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
                onAuthenticated = { navigator.replaceBackStackWith(MainNavKey) },
            )
            signInWithEmailEntries(
                onAuthenticated = { navigator.replaceBackStackWith(MainNavKey) },
            )
            mainEntries(
                onNavigate = navigator::navigateTo,
                onSignedOut = { navigator.replaceBackStackWith(AuthenticationNavKey) },
            )
            downloadsEntries(onBack = navigator::navigateBack, onNavigate = navigator::navigateTo)
            playerEntries(onBack = navigator::navigateBack, onNavigate = navigator::navigateTo)
        },
    )
}