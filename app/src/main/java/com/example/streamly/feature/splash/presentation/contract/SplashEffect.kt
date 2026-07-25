package com.example.streamly.feature.splash.presentation.contract

sealed interface SplashEffect {
    data object NavigateToHome : SplashEffect
    data object NavigateToAuthentication : SplashEffect
}