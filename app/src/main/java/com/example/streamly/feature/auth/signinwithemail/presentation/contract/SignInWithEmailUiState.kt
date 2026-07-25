package com.example.streamly.feature.auth.signinwithemail.presentation.contract

data class SignInWithEmailUiState(
    val name: String? = null,
    val email: String? = null,
) {
    val isContinueButtonEnabled: Boolean
        get() = name?.isNotBlank() == true && email?.contains("@") == true
}