package com.example.streamly.feature.profile.presentation.contract

sealed interface ProfileIntent {
    data object BackClicked : ProfileIntent
    data object DownloadsClicked : ProfileIntent
    data object SignOutClicked : ProfileIntent
    data object SignOutConfirmed : ProfileIntent
    data object SignOutDismissed : ProfileIntent
}