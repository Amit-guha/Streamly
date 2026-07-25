package com.example.streamly.feature.auth.authentication.presentation.contract

sealed interface AuthenticationIntent {
    data object OnContinueWithGoogleButtonClicked : AuthenticationIntent
    data object OnSignInWithEmailButtonClicked : AuthenticationIntent
    data object OnContinueAsGuestButtonClicked : AuthenticationIntent
}