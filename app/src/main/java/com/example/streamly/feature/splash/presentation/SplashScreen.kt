package com.example.streamly.feature.splash.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.streamly.MainNavKey
import com.example.streamly.core.designsystem.component.CircularCommonLoader
import com.example.streamly.core.navigation.NavigationDestination
import com.example.streamly.feature.auth.authentication.presentation.navigation.AuthenticationNavKey
import com.example.streamly.feature.splash.presentation.contract.SplashEffect
import com.example.streamly.feature.splash.presentation.contract.SplashUiState
import com.example.streamly.ui.theme.StreamlyTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun SplashScreenRoute(
    onNavigate: (NavigationDestination) -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                SplashEffect.NavigateToHome -> onNavigate(MainNavKey)
                SplashEffect.NavigateToAuthentication -> onNavigate(AuthenticationNavKey)
            }
        }
    }

    SplashScreen(uiState = SplashUiState)
}

@Composable
fun SplashScreen(
    uiState: SplashUiState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.secondary,
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        CircularCommonLoader(
            indicatorColor = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@Preview(name = "Mobile", device = Devices.PHONE)
@Composable
private fun SplashScreenMobilePreview() {
    StreamlyTheme {
        SplashScreen(uiState = SplashUiState)
    }
}

@Preview(name = "Foldable", device = Devices.FOLDABLE)
@Composable
private fun SplashScreenFoldablePreview() {
    StreamlyTheme {
        SplashScreen(uiState = SplashUiState)
    }
}

@Preview(name = "Tablet", device = Devices.TABLET)
@Composable
private fun SplashScreenTabletPreview() {
    StreamlyTheme {
        SplashScreen(uiState = SplashUiState)
    }
}