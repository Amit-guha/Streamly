package com.example.streamly.feature.home.presentation.contract

sealed interface HomeIntent {
    data object NavigateToProfileClicked : HomeIntent
}