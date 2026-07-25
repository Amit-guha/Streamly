package com.example.streamly.feature.profile.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.streamly.R
import com.example.streamly.feature.profile.presentation.contract.ProfileEffect
import com.example.streamly.feature.profile.presentation.contract.ProfileIntent
import com.example.streamly.feature.profile.presentation.contract.ProfileUiState
import com.example.streamly.ui.theme.StreamlyTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun ProfileScreenRoute(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                ProfileEffect.NavigateBack -> onBack()
            }
        }
    }

    ProfileScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
    )
}

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onIntent: (ProfileIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        ) {
            Text(text = stringResource(R.string.profile_message))
            Button(onClick = { onIntent(ProfileIntent.BackClicked) }) {
                Text(text = stringResource(R.string.profile_back))
            }
        }
    }
}

@Preview(name = "Mobile", device = Devices.PHONE)
@Composable
private fun ProfileScreenMobilePreview() {
    StreamlyTheme {
        ProfileScreen(uiState = ProfileUiState, onIntent = {})
    }
}

@Preview(name = "Foldable", device = Devices.FOLDABLE)
@Composable
private fun ProfileScreenFoldablePreview() {
    StreamlyTheme {
        ProfileScreen(uiState = ProfileUiState, onIntent = {})
    }
}

@Preview(name = "Tablet", device = Devices.TABLET)
@Composable
private fun ProfileScreenTabletPreview() {
    StreamlyTheme {
        ProfileScreen(uiState = ProfileUiState, onIntent = {})
    }
}