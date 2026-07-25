package com.example.streamly.feature.splash.domain.usecase

import com.example.streamly.core.domain.storage.datastore.AppPreferences
import javax.inject.Inject

class IsLoggedInUseCase @Inject constructor(
    private val appPreferences: AppPreferences,
) {
    suspend operator fun invoke(): Boolean = appPreferences.isLoggedIn()
}