package com.example.streamly.feature.profile.presentation.contract

sealed interface ProfileEffect {
    data object NavigateBack : ProfileEffect
}