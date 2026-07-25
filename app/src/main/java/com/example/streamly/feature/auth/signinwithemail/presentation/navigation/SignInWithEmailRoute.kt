package com.example.streamly.feature.auth.signinwithemail.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.example.streamly.core.navigation.NavigationDestination
import com.example.streamly.feature.auth.signinwithemail.presentation.SignInWithEmailScreenRoute
import kotlinx.serialization.Serializable

@Serializable
data object SignInWithEmailNavKey : NavigationDestination

fun EntryProviderScope<NavigationDestination>.signInWithEmailEntries(
    onAuthenticated: () -> Unit,
) {
    entry<SignInWithEmailNavKey> {
        SignInWithEmailScreenRoute(onAuthenticated = onAuthenticated)
    }
}