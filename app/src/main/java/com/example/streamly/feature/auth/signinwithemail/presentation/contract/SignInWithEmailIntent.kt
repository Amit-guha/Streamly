package com.example.streamly.feature.auth.signinwithemail.presentation.contract

sealed interface SignInWithEmailIntent {
    data class OnNameChanged(val name: String) : SignInWithEmailIntent
    data class OnEmailChanged(val email: String) : SignInWithEmailIntent
    data object OnContinueButtonClicked : SignInWithEmailIntent
}