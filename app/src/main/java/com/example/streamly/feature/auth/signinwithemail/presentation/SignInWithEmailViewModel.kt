package com.example.streamly.feature.auth.signinwithemail.presentation

import androidx.lifecycle.viewModelScope
import com.example.streamly.core.common.base.MVIViewModel
import com.example.streamly.feature.auth.signinwithemail.domain.usecase.SignInWithEmailUseCase
import com.example.streamly.feature.auth.signinwithemail.presentation.contract.SignInWithEmailEffect
import com.example.streamly.feature.auth.signinwithemail.presentation.contract.SignInWithEmailIntent
import com.example.streamly.feature.auth.signinwithemail.presentation.contract.SignInWithEmailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SignInWithEmailViewModel @Inject constructor(
    private val signInWithEmailUseCase: SignInWithEmailUseCase,
) : MVIViewModel<SignInWithEmailUiState, SignInWithEmailIntent, SignInWithEmailEffect>(
    initialState = SignInWithEmailUiState(),
) {

    override fun onIntent(intent: SignInWithEmailIntent) {
        when (intent) {
            is SignInWithEmailIntent.OnNameChanged -> _state.update { it.copy(name = intent.name) }
            is SignInWithEmailIntent.OnEmailChanged -> _state.update { it.copy(email = intent.email) }
            is SignInWithEmailIntent.OnContinueButtonClicked -> saveProfileAndContinue()
        }
    }

    private fun saveProfileAndContinue() {
        val current = state.value
        if (!current.isContinueButtonEnabled) return

        val name = current.name
        val email = current.email
        if (name == null || email == null) return

        viewModelScope.launch {
            signInWithEmailUseCase(name = name, email = email)
            sendEffect(SignInWithEmailEffect.NavigateToHome)
        }
    }
}