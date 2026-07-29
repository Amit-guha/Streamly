package com.example.streamly

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@Composable
fun MainScreen(mainViewModel: MainViewModel = hiltViewModel()) {
    val isOffline by mainViewModel.isOffline.collectAsStateWithLifecycle()
    val offlineSnackbarHostState = remember { SnackbarHostState() }
    val offlineMessage = stringResource(R.string.connectivity_offline_message)

    LaunchedEffect(isOffline) {
        if (isOffline) {
            offlineSnackbarHostState.showSnackbar(
                message = offlineMessage,
                duration = SnackbarDuration.Short,
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        StreamlyNavHost()
        SnackbarHost(
            hostState = offlineSnackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.systemBars),
        )
    }
}