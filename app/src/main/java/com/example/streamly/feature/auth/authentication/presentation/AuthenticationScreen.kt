package com.example.streamly.feature.auth.authentication.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.streamly.R
import com.example.streamly.core.designsystem.LocalWindowSizeClass
import com.example.streamly.core.navigation.NavigationDestination
import com.example.streamly.feature.auth.authentication.presentation.contract.AuthenticationEffect
import com.example.streamly.feature.auth.authentication.presentation.contract.AuthenticationIntent
import com.example.streamly.feature.auth.authentication.presentation.contract.AuthenticationUiState
import com.example.streamly.feature.auth.signinwithemail.presentation.navigation.SignInWithEmailNavKey
import com.example.streamly.ui.theme.StreamlyTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
internal fun AuthenticationScreenRoute(
    onNavigate: (NavigationDestination) -> Unit,
    onAuthenticated: () -> Unit,
    viewModel: AuthenticationViewModel = hiltViewModel(),
) {
    val windowSizeClass = LocalWindowSizeClass.current

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                AuthenticationEffect.NavigateToHome -> onAuthenticated()
                AuthenticationEffect.NavigateToEmailSignIn -> onNavigate(SignInWithEmailNavKey)
            }
        }
    }

    RequestNotificationPermission()
    AuthenticationScreen(
        uiState = AuthenticationUiState,
        windowSizeClass = windowSizeClass,
        onIntent = viewModel::onIntent,
    )
}

@Composable
private fun RequestNotificationPermission() {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return@LaunchedEffect
        val isGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
        if (!isGranted) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun AuthenticationScreen(
    uiState: AuthenticationUiState,
    windowSizeClass: WindowSizeClass,
    onIntent: (AuthenticationIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Compact-width phones fill the available width; anything wider (foldable/tablet) caps at a
    // readable card width instead of stretching the form edge-to-edge, matching ProfileScreen's
    // isWideLayout pattern.
    val isWideLayout = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact

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
    ) {
        // The scrollable Column is sized to the full screen so it has a bounded height to scroll
        // within; Arrangement.Center then centers the (usually shorter) inner content by default,
        // and only scrolls once the content taller than the viewport (e.g. short landscape).
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Column(
                modifier = Modifier
                    .then(if (isWideLayout) Modifier.widthIn(max = 480.dp) else Modifier)
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(20.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.app_name_initial),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }

                Text(
                    text = stringResource(R.string.onboarding_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = stringResource(R.string.onboarding_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                )

                Button(
                    onClick = { onIntent(AuthenticationIntent.OnContinueWithGoogleButtonClicked) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary,
                        contentColor = MaterialTheme.colorScheme.primary,
                    ),
                ) {
                    Text(stringResource(R.string.onboarding_continue_with_google))
                }

                OutlinedButton(
                    onClick = { onIntent(AuthenticationIntent.OnSignInWithEmailButtonClicked) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                        brush = SolidColor(MaterialTheme.colorScheme.onPrimary),
                    ),
                ) {
                    Text(stringResource(R.string.onboarding_sign_in_with_email))
                }

                TextButton(onClick = { onIntent(AuthenticationIntent.OnContinueAsGuestButtonClicked) }) {
                    Text(
                        text = stringResource(R.string.onboarding_continue_as_guest),
                        color = MaterialTheme.colorScheme.onPrimary,
                        textDecoration = TextDecoration.Underline,
                    )
                }
            }
        }
    }
}

private val sampleUiState = AuthenticationUiState

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(name = "Mobile", device = Devices.PHONE)
@Composable
private fun AuthenticationScreenMobilePreview() {
    StreamlyTheme {
        AuthenticationScreen(
            uiState = sampleUiState,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
            onIntent = {},
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(name = "Foldable", device = Devices.FOLDABLE)
@Composable
private fun AuthenticationScreenFoldablePreview() {
    StreamlyTheme {
        AuthenticationScreen(
            uiState = sampleUiState,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(673.dp, 841.dp)),
            onIntent = {},
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(name = "Tablet", device = Devices.TABLET)
@Composable
private fun AuthenticationScreenTabletPreview() {
    StreamlyTheme {
        AuthenticationScreen(
            uiState = sampleUiState,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(1280.dp, 800.dp)),
            onIntent = {},
        )
    }
}