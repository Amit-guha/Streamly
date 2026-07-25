package com.example.streamly.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.rememberNavBackStack

@Stable
class AppNavigator(
    val backStack: NavBackStack<NavigationDestination>,
) {
    fun navigateTo(destination: NavigationDestination) {
        backStack.add(destination)
    }

    fun navigateBack() {
        backStack.removeLastOrNull()
    }

    /** Clears the whole back stack and starts fresh at [destination]. Used by Splash so it never
     * remains in the stack once the real start destination is resolved. */
    fun replaceBackStackWith(destination: NavigationDestination) {
        backStack.clear()
        backStack.add(destination)
    }
}

@Composable
fun rememberAppNavigator(startDestination: NavigationDestination): AppNavigator {
    val backStack = rememberNavBackStack(startDestination)
    return remember(backStack) { AppNavigator(backStack) }
}