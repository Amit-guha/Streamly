package com.example.streamly.feature.profile.presentation

import androidx.lifecycle.viewModelScope
import com.example.streamly.core.common.base.MVIViewModel
import com.example.streamly.feature.profile.domain.usecase.ObserveUserProfileUseCase
import com.example.streamly.feature.profile.presentation.contract.ProfileEffect
import com.example.streamly.feature.profile.presentation.contract.ProfileIntent
import com.example.streamly.feature.profile.presentation.contract.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    observeUserProfileUseCase: ObserveUserProfileUseCase,
) : MVIViewModel<ProfileUiState, ProfileIntent, ProfileEffect>(initialState = ProfileUiState()) {

    init {
        viewModelScope.launch {
            observeUserProfileUseCase().collect { profile ->
                _state.update { it.copy(name = profile.name, email = profile.email) }
            }
        }
    }

    override fun onIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.BackClicked -> sendEffect(ProfileEffect.NavigateBack)
        }
    }
}