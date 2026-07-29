package com.example.streamly.feature.profile.presentation

import androidx.lifecycle.viewModelScope
import com.example.streamly.core.common.base.MVIViewModel
import com.example.streamly.feature.downloads.domain.usecase.ClearDownloadsUseCase
import com.example.streamly.feature.profile.domain.usecase.GetUserProfileUseCase
import com.example.streamly.feature.profile.domain.usecase.SignOutUseCase
import com.example.streamly.feature.profile.presentation.contract.ProfileEffect
import com.example.streamly.feature.profile.presentation.contract.ProfileIntent
import com.example.streamly.feature.profile.presentation.contract.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    getUserProfileUseCase: GetUserProfileUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val clearDownloadsUseCase: ClearDownloadsUseCase,
) : MVIViewModel<ProfileUiState, ProfileIntent, ProfileEffect>(initialState = ProfileUiState()) {

    private var profileObservationJob: Job? = null

    init {
        profileObservationJob = viewModelScope.launch {
            getUserProfileUseCase().collect { profile ->
                if (profile.name.isNullOrBlank() && profile.email.isNullOrBlank()) return@collect
                _state.update { it.copy(name = profile.name, email = profile.email) }
            }
        }
    }

    override fun onIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.BackClicked -> sendEffect(ProfileEffect.NavigateBack)
            ProfileIntent.DownloadsClicked -> sendEffect(ProfileEffect.NavigateToDownloads)
            ProfileIntent.SignOutClicked -> _state.update { it.copy(isSignOutDialogVisible = true) }
            ProfileIntent.SignOutDismissed -> _state.update { it.copy(isSignOutDialogVisible = false) }
            ProfileIntent.SignOutConfirmed -> signOut()
        }
    }

    private fun signOut() {
        profileObservationJob?.cancel()
        _state.update { it.copy(isSignOutDialogVisible = false, isSigningOut = true) }
        viewModelScope.launch {
            clearDownloadsUseCase()
            signOutUseCase()
            sendEffect(ProfileEffect.NavigateToOnboarding)
        }
    }
}