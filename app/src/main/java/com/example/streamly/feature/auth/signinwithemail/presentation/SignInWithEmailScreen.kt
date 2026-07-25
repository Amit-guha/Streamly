package com.example.streamly.feature.auth.signinwithemail.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.streamly.R
import com.example.streamly.feature.auth.signinwithemail.presentation.contract.SignInWithEmailEffect
import com.example.streamly.feature.auth.signinwithemail.presentation.contract.SignInWithEmailIntent
import com.example.streamly.feature.auth.signinwithemail.presentation.contract.SignInWithEmailUiState
import com.example.streamly.ui.theme.StreamlyTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun SignInWithEmailScreenRoute(
    onAuthenticated: () -> Unit,
    viewModel: SignInWithEmailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                SignInWithEmailEffect.NavigateToHome -> onAuthenticated()
            }
        }
    }

    SignInWithEmailScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
    )
}

@Composable
fun SignInWithEmailScreen(
    uiState: SignInWithEmailUiState,
    onIntent: (SignInWithEmailIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.email_sign_in_title),
                style = MaterialTheme.typography.headlineSmall,
            )

            OutlinedTextField(
                value = uiState.name.orEmpty(),
                onValueChange = { onIntent(SignInWithEmailIntent.OnNameChanged(it)) },
                label = { Text(stringResource(R.string.email_sign_in_name_label)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = uiState.email.orEmpty(),
                onValueChange = { onIntent(SignInWithEmailIntent.OnEmailChanged(it)) },
                label = { Text(stringResource(R.string.email_sign_in_email_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                onClick = { onIntent(SignInWithEmailIntent.OnContinueButtonClicked) },
                enabled = uiState.isContinueButtonEnabled,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.email_sign_in_continue))
            }
        }
    }
}

@Preview(name = "Mobile", device = Devices.PHONE)
@Composable
private fun SignInWithEmailScreenMobilePreview() {
    StreamlyTheme {
        SignInWithEmailScreen(
            uiState = SignInWithEmailUiState(name = "Amit Guha", email = "amit@example.com"),
            onIntent = {},
        )
    }
}

@Preview(name = "Foldable", device = Devices.FOLDABLE)
@Composable
private fun SignInWithEmailScreenFoldablePreview() {
    StreamlyTheme {
        SignInWithEmailScreen(
            uiState = SignInWithEmailUiState(name = "Amit Guha", email = "amit@example.com"),
            onIntent = {},
        )
    }
}

@Preview(name = "Tablet", device = Devices.TABLET)
@Composable
private fun SignInWithEmailScreenTabletPreview() {
    StreamlyTheme {
        SignInWithEmailScreen(
            uiState = SignInWithEmailUiState(name = "Amit Guha", email = "amit@example.com"),
            onIntent = {},
        )
    }
}