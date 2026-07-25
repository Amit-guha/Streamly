package com.example.streamly.feature.profile.presentation

import com.example.streamly.core.common.base.MVIViewModel
import com.example.streamly.feature.profile.presentation.contract.ProfileEffect
import com.example.streamly.feature.profile.presentation.contract.ProfileIntent
import com.example.streamly.feature.profile.presentation.contract.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() :
    MVIViewModel<ProfileUiState, ProfileIntent, ProfileEffect>(initialState = ProfileUiState) {

    override fun onIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.BackClicked -> sendEffect(ProfileEffect.NavigateBack)
        }
    }
}