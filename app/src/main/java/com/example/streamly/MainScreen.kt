package com.example.streamly

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.EntryProviderScope
import com.example.streamly.core.designsystem.LocalWindowSizeClass
import com.example.streamly.core.navigation.NavigationDestination
import com.example.streamly.feature.downloads.presentation.navigation.DownloadsNavKey
import com.example.streamly.feature.home.presentation.HomeScreenRoute
import com.example.streamly.feature.profile.presentation.ProfileScreenRoute
import com.example.streamly.feature.shorts.presentation.ShortsScreenRoute
import kotlinx.serialization.Serializable

@Serializable
data object MainNavKey : NavigationDestination

fun EntryProviderScope<NavigationDestination>.mainEntries(
    onNavigate: (NavigationDestination) -> Unit,
    onSignedOut: () -> Unit,
) {
    entry<MainNavKey> {
        MainScreenRoute(onNavigate = onNavigate, onSignedOut = onSignedOut)
    }
}

private enum class MainTab(val labelRes: Int, val icon: ImageVector) {
    HOME(R.string.bottom_nav_home, Icons.Filled.Home),
    SHORTS(R.string.bottom_nav_shorts, Icons.Filled.PlayCircle),
    PROFILE(R.string.bottom_nav_you, Icons.Filled.AccountCircle),
}

/**
 * The tabbed root shown once the user is authenticated. Home/Shorts/Profile are switched via
 * local [selectedTab] state, not the outer [NavigationDestination] back stack, so the nav stays
 * put and each tab's own scroll/UI state survives switching away and back (see
 * [rememberSaveableStateHolder]). Downloads and Player still push onto the real back stack from
 * here (via [onNavigate]), which is what hides the nav while they're open.
 *
 * Compact width (phone portrait) gets a bottom [NavigationBar]; genuine Medium/Expanded screens
 * (foldables unfolded, tablets) switch to a side [NavigationRail] instead — a bottom bar wastes
 * horizontal space and isn't the Material3-recommended pattern once there's width to spare. A
 * phone rotated to landscape also reports Expanded *width*, but its *height* stays Compact (it's
 * still a short screen, just sideways) — checking height too keeps a landscape phone on the
 * familiar bottom bar instead of switching it to a rail like a real tablet/foldable.
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
internal fun MainScreenRoute(
    onNavigate: (NavigationDestination) -> Unit,
    onSignedOut: () -> Unit,
) {
    val windowSizeClass = LocalWindowSizeClass.current
    val isCompactHeight = windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact
    val useNavigationRail = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact && !isCompactHeight
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.HOME) }
    val tabStateHolder = rememberSaveableStateHolder()

    val tabContent: @Composable () -> Unit = {
        tabStateHolder.SaveableStateProvider(selectedTab.name) {
            when (selectedTab) {
                MainTab.HOME -> HomeScreenRoute(onNavigate = onNavigate)
                MainTab.SHORTS -> ShortsScreenRoute(onBack = { selectedTab = MainTab.HOME })
                MainTab.PROFILE -> ProfileScreenRoute(
                    onBack = { selectedTab = MainTab.HOME },
                    onNavigateToDownloads = { onNavigate(DownloadsNavKey) },
                    onSignedOut = onSignedOut,
                )
            }
        }
    }

    if (!useNavigationRail) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar {
                    MainTab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            icon = { Icon(imageVector = tab.icon, contentDescription = null) },
                            label = { Text(stringResource(tab.labelRes)) },
                        )
                    }
                }
            },
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                tabContent()
            }
        }
    } else {
        Row(modifier = Modifier.fillMaxSize()) {
            NavigationRail {
                MainTab.entries.forEach { tab ->
                    NavigationRailItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(imageVector = tab.icon, contentDescription = null) },
                        label = { Text(stringResource(tab.labelRes)) },
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
            ) {
                tabContent()
            }
        }
    }
}