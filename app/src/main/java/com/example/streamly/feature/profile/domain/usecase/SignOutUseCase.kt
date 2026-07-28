package com.example.streamly.feature.profile.domain.usecase

import com.example.streamly.core.domain.storage.datastore.AppPreferences
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val appPreferences: AppPreferences,
) {
    suspend operator fun invoke() {
        appPreferences.clearSession()
    }
}