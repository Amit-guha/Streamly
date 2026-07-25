package com.example.streamly.feature.auth.authentication.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.streamly.R
import com.example.streamly.core.navigation.NavigationDestination
import com.example.streamly.feature.auth.authentication.presentation.contract.AuthenticationEffect
import com.example.streamly.feature.auth.authentication.presentation.contract.AuthenticationIntent
import com.example.streamly.feature.auth.authentication.presentation.contract.AuthenticationUiState
import com.example.streamly.feature.auth.signinwithemail.presentation.navigation.SignInWithEmailNavKey
import com.example.streamly.ui.theme.StreamlyTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun AuthenticationScreenRoute(
    onNavigate: (NavigationDestination) -> Unit,
    onAuthenticated: () -> Unit,
    viewModel: AuthenticationViewModel = hiltViewModel(),
) {
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                AuthenticationEffect.NavigateToHome -> onAuthenticated()
                AuthenticationEffect.NavigateToEmailSignIn -> onNavigate(SignInWithEmailNavKey)
            }
        }
    }

    AuthenticationScreen(
        uiState = AuthenticationUiState,
        onIntent = viewModel::onIntent,
    )
}

@Composable
fun AuthenticationScreen(
    uiState: AuthenticationUiState,
    onIntent: (AuthenticationIntent) -> Unit,
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
        Column(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp),
                    )
            )

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

@Preview(name = "Mobile", device = Devices.PHONE)
@Composable
private fun AuthenticationScreenMobilePreview() {
    StreamlyTheme {
        AuthenticationScreen(uiState = AuthenticationUiState, onIntent = {})
    }
}

@Preview(name = "Foldable", device = Devices.FOLDABLE)
@Composable
private fun AuthenticationScreenFoldablePreview() {
    StreamlyTheme {
        AuthenticationScreen(uiState = AuthenticationUiState, onIntent = {})
    }
}

@Preview(name = "Tablet", device = Devices.TABLET)
@Composable
private fun AuthenticationScreenTabletPreview() {
    StreamlyTheme {
        AuthenticationScreen(uiState = AuthenticationUiState, onIntent = {})
    }
}