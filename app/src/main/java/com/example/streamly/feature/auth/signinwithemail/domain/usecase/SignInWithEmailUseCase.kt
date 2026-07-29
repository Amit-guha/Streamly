package com.example.streamly.feature.auth.signinwithemail.domain.usecase

import com.example.streamly.core.common.enum.UserType
import com.example.streamly.core.domain.storage.datastore.AppPreferences
import javax.inject.Inject


class SignInWithEmailUseCase @Inject constructor(
    private val appPreferences: AppPreferences,
) {
    suspend operator fun invoke(name: String, email: String) {
        appPreferences.saveUserProfile(name = name.trim(), email = email.trim())
        appPreferences.setUserType(UserType.EMAIL)
        appPreferences.setLoggedIn(true)
    }
}