package com.example.streamly.feature.splash.presentation

import androidx.lifecycle.viewModelScope
import com.example.streamly.core.common.base.MVIViewModel
import com.example.streamly.core.common.constant.AppConstants
import com.example.streamly.feature.splash.domain.usecase.IsLoggedInUseCase
import com.example.streamly.feature.splash.presentation.contract.SplashEffect
import com.example.streamly.feature.splash.presentation.contract.SplashIntent
import com.example.streamly.feature.splash.presentation.contract.SplashUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val isLoggedInUseCase: IsLoggedInUseCase,
) : MVIViewModel<SplashUiState, SplashIntent, SplashEffect>(initialState = SplashUiState) {

    init {
        viewModelScope.launch {
            delay(AppConstants.SPLASH_MIN_DISPLAY_DURATION)

            val effect = if (isLoggedInUseCase()) {
                SplashEffect.NavigateToHome
            } else {
                SplashEffect.NavigateToAuthentication
            }
            sendEffect(effect)
        }
    }

    override fun onIntent(intent: SplashIntent) {}
}