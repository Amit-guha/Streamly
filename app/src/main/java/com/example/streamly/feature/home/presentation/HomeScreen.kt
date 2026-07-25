package com.example.streamly.feature.home.presentation

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
import com.example.streamly.core.navigation.NavigationDestination
import com.example.streamly.feature.home.presentation.contract.HomeEffect
import com.example.streamly.feature.home.presentation.contract.HomeIntent
import com.example.streamly.feature.home.presentation.contract.HomeUiState
import com.example.streamly.feature.profile.presentation.navigation.ProfileNavKey
import com.example.streamly.ui.theme.StreamlyTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun HomeScreenRoute(
    onNavigate: (NavigationDestination) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                HomeEffect.NavigateToProfile -> onNavigate(ProfileNavKey)
            }
        }
    }

    HomeScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
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
            Text(text = stringResource(R.string.home_greeting, uiState.greetingName))
            Button(onClick = { onIntent(HomeIntent.NavigateToProfileClicked) }) {
                Text(text = stringResource(R.string.home_go_to_profile))
            }
        }
    }
}

@Preview(name = "Mobile", device = Devices.PHONE)
@Composable
private fun HomeScreenMobilePreview() {
    StreamlyTheme {
        HomeScreen(uiState = HomeUiState(greetingName = "Android"), onIntent = {})
    }
}

@Preview(name = "Foldable", device = Devices.FOLDABLE)
@Composable
private fun HomeScreenFoldablePreview() {
    StreamlyTheme {
        HomeScreen(uiState = HomeUiState(greetingName = "Android"), onIntent = {})
    }
}

@Preview(name = "Tablet", device = Devices.TABLET)
@Composable
private fun HomeScreenTabletPreview() {
    StreamlyTheme {
        HomeScreen(uiState = HomeUiState(greetingName = "Android"), onIntent = {})
    }
}