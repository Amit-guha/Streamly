package com.example.streamly.feature.home.presentation

import com.example.streamly.core.common.base.MVIViewModel
import com.example.streamly.feature.home.presentation.contract.HomeEffect
import com.example.streamly.feature.home.presentation.contract.HomeIntent
import com.example.streamly.feature.home.presentation.contract.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() :
    MVIViewModel<HomeUiState, HomeIntent, HomeEffect>(initialState = HomeUiState()) {

    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.NavigateToProfileClicked -> sendEffect(HomeEffect.NavigateToProfile)
        }
    }
}