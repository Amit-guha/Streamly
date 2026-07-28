package com.example.streamly.feature.profile.presentation.contract

data class ProfileUiState(
    val name: String? = null,
    val email: String? = null,
    val isSignOutDialogVisible: Boolean = false,
)