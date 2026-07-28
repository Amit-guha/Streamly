package com.example.streamly.feature.profile.presentation.contract

sealed interface ProfileEffect {
    data object NavigateBack : ProfileEffect
    data object NavigateToDownloads : ProfileEffect
    data object NavigateToOnboarding : ProfileEffect
}