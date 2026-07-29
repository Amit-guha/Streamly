package com.example.streamly.feature.auth.authentication.presentation

import androidx.lifecycle.viewModelScope
import com.example.streamly.core.common.base.MVIViewModel
import com.example.streamly.core.common.enum.UserType
import com.example.streamly.feature.auth.authentication.domain.usecase.SignInUseCase
import com.example.streamly.feature.auth.authentication.presentation.contract.AuthenticationEffect
import com.example.streamly.feature.auth.authentication.presentation.contract.AuthenticationIntent
import com.example.streamly.feature.auth.authentication.presentation.contract.AuthenticationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
) : MVIViewModel<AuthenticationUiState, AuthenticationIntent, AuthenticationEffect>(
    initialState = AuthenticationUiState,
) {

    override fun onIntent(intent: AuthenticationIntent) {
        when (intent) {
            AuthenticationIntent.OnContinueWithGoogleButtonClicked -> signInAndContinue(UserType.GOOGLE)
            AuthenticationIntent.OnContinueAsGuestButtonClicked -> signInAndContinue(UserType.GUEST)
            AuthenticationIntent.OnSignInWithEmailButtonClicked ->
                sendEffect(AuthenticationEffect.NavigateToEmailSignIn)
        }
    }

    private fun signInAndContinue(userType: UserType) {
        viewModelScope.launch {
            signInUseCase(userType)
            sendEffect(AuthenticationEffect.NavigateToHome)
        }
    }
}