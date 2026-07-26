package com.example.streamly.core.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay

private const val TRANSITION_DURATION_MILLIS = 300

// Material's "shared axis" pattern uses a partial slide (not a full screen width) paired with
// a fade, so it reads as a subtle directional shift rather than a slow, heavy full-screen slide.
private const val SLIDE_DISTANCE_DIVISOR = 4

@Composable
fun AppNavGraph(
    backStack: NavBackStack<NavigationDestination>,
    onBack: () -> Unit,
    entryProvider: (NavigationDestination) -> NavEntry<NavigationDestination>,
    modifier: Modifier = Modifier,
) {
    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = onBack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        predictivePopTransitionSpec = {
            ContentTransform(
                targetContentEnter = slideInHorizontally(
                    animationSpec = tween(TRANSITION_DURATION_MILLIS),
                    initialOffsetX = { fullWidth -> -fullWidth / SLIDE_DISTANCE_DIVISOR },
                ) + fadeIn(animationSpec = tween(TRANSITION_DURATION_MILLIS)),
                initialContentExit = slideOutHorizontally(
                    animationSpec = tween(TRANSITION_DURATION_MILLIS),
                    targetOffsetX = { fullWidth -> fullWidth / SLIDE_DISTANCE_DIVISOR },
                ) + fadeOut(animationSpec = tween(TRANSITION_DURATION_MILLIS)),
            )
        },
        entryProvider = entryProvider,
    )
}