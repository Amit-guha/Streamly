package com.example.streamly.feature.auth.authentication.presentation.contract

sealed interface AuthenticationEffect {
    data object NavigateToHome : AuthenticationEffect
    data object NavigateToEmailSignIn : AuthenticationEffect
}