package com.example.streamly.feature.auth.signinwithemail.presentation.contract

sealed interface SignInWithEmailEffect {
    data object NavigateToHome : SignInWithEmailEffect
}