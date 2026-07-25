package com.example.streamly.core.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay

private const val TRANSITION_DURATION_MILLIS = 700

/**
 * Wraps [NavDisplay] with a [rememberViewModelStoreNavEntryDecorator] so every entry's
 * `hiltViewModel()` is scoped to that back-stack entry instead of the host Activity.
 *
 * [predictivePopTransitionSpec] is overridden to the same crossfade as the default
 * push/pop transitions. Nav3's built-in predictive-back default shrinks the outgoing
 * screen to 70% scale, which looks inconsistent with our flat, shadow-less Scaffolds
 * when triggered by the system back gesture/button.
 */
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
                targetContentEnter = fadeIn(animationSpec = tween(TRANSITION_DURATION_MILLIS)),
                initialContentExit = fadeOut(animationSpec = tween(TRANSITION_DURATION_MILLIS)),
            )
        },
        entryProvider = entryProvider,
    )
}