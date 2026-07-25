package com.example.streamly.feature.auth.signinwithemail.domain.usecase

import com.example.streamly.core.domain.storage.datastore.AppPreferences
import javax.inject.Inject


class SaveUserProfileUseCase @Inject constructor(
    private val appPreferences: AppPreferences,
) {
    suspend operator fun invoke(name: String, email: String) {
        appPreferences.saveUserProfile(name = name.trim(), email = email.trim())
        appPreferences.setLoggedIn(true)
    }
}