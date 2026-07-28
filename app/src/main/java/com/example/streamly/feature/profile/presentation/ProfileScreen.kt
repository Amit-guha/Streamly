package com.example.streamly.feature.profile.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.streamly.R
import com.example.streamly.core.designsystem.LocalWindowSizeClass
import com.example.streamly.feature.profile.presentation.component.ProfileHeader
import com.example.streamly.feature.profile.presentation.component.ProfileMenuItem
import com.example.streamly.feature.profile.presentation.component.SignOutConfirmationDialog
import com.example.streamly.feature.profile.presentation.contract.ProfileEffect
import com.example.streamly.feature.profile.presentation.contract.ProfileIntent
import com.example.streamly.feature.profile.presentation.contract.ProfileUiState
import com.example.streamly.ui.theme.StreamlyTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
internal fun ProfileScreenRoute(
    onBack: () -> Unit,
    onNavigateToDownloads: () -> Unit,
    onSignedOut: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val windowSizeClass = LocalWindowSizeClass.current

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                ProfileEffect.NavigateBack -> onBack()
                ProfileEffect.NavigateToDownloads -> onNavigateToDownloads()
                ProfileEffect.NavigateToOnboarding -> onSignedOut()
            }
        }
    }

    ProfileScreen(
        uiState = uiState,
        windowSizeClass = windowSizeClass,
        onIntent = viewModel::onIntent,
    )
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    windowSizeClass: WindowSizeClass,
    onIntent: (ProfileIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isWideLayout = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.navigationBars,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (isWideLayout) Modifier.widthIn(max = 640.dp) else Modifier),
            ) {
                ProfileHeader(
                    name = uiState.name,
                    email = uiState.email,
                    onBackButtonClicked = { onIntent(ProfileIntent.BackClicked) },
                )

                ProfileMenuItem(
                    icon = Icons.Filled.Download,
                    label = stringResource(R.string.profile_downloads),
                    enabled = true,
                    onClick = { onIntent(ProfileIntent.DownloadsClicked) },
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                ProfileMenuItem(
                    icon = Icons.Filled.History,
                    label = stringResource(R.string.profile_watch_history),
                    enabled = false,
                    onClick = {},
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                ProfileMenuItem(
                    icon = Icons.Filled.Settings,
                    label = stringResource(R.string.profile_settings),
                    enabled = false,
                    onClick = {},
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = stringResource(R.string.profile_sign_out),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onIntent(ProfileIntent.SignOutClicked) }
                        .padding(16.dp),
                )
            }
        }
    }

    if (uiState.isSignOutDialogVisible) {
        SignOutConfirmationDialog(
            onConfirm = { onIntent(ProfileIntent.SignOutConfirmed) },
            onDismiss = { onIntent(ProfileIntent.SignOutDismissed) },
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(name = "Mobile", device = Devices.PHONE)
@Composable
private fun ProfileScreenMobilePreview() {
    StreamlyTheme {
        ProfileScreen(
            uiState = ProfileUiState(name = "Anika Rahman", email = "anika@streamly.app"),
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
            onIntent = {},
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(name = "Foldable", device = Devices.FOLDABLE)
@Composable
private fun ProfileScreenFoldablePreview() {
    StreamlyTheme {
        ProfileScreen(
            uiState = ProfileUiState(name = "Anika Rahman", email = "anika@streamly.app"),
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(673.dp, 841.dp)),
            onIntent = {},
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(name = "Tablet", device = Devices.TABLET)
@Composable
private fun ProfileScreenTabletPreview() {
    StreamlyTheme {
        ProfileScreen(
            uiState = ProfileUiState(name = "Anika Rahman", email = "anika@streamly.app"),
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(1280.dp, 800.dp)),
            onIntent = {},
        )
    }
}