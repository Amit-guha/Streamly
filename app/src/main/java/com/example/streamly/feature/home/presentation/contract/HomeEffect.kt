package com.example.streamly.feature.home.presentation.contract

sealed interface HomeEffect {
    data object NavigateToProfile : HomeEffect
}