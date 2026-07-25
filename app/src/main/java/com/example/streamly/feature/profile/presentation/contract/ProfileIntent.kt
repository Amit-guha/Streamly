package com.example.streamly.feature.profile.presentation.contract

sealed interface ProfileIntent {
    data object BackClicked : ProfileIntent
}