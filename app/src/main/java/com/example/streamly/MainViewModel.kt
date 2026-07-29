package com.example.streamly

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.streamly.core.common.constant.AppConstants
import com.example.streamly.core.domain.connectivity.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** Backs [MainScreen]'s app-wide "You're offline" snackbar. A plain [ViewModel], not the
 * feature [com.example.streamly.core.common.base.MVIViewModel]: it just mirrors one upstream
 * flow app-wide, with no per-screen user intents or one-time effects to model. */
@HiltViewModel
class MainViewModel @Inject constructor(
    networkMonitor: NetworkMonitor,
) : ViewModel() {

    val isOffline: StateFlow<Boolean> = networkMonitor.isOnline
        .map { isOnline -> !isOnline }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(AppConstants.CONNECTIVITY_STOP_TIMEOUT_MILLIS),
            initialValue = false,
        )
}