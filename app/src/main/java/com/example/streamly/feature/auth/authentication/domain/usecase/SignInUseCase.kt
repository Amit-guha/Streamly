package com.example.streamly.feature.auth.authentication.domain.usecase

import com.example.streamly.core.domain.storage.datastore.AppPreferences
import javax.inject.Inject

/** Marks the session as signed in. Used by the Google and guest paths — neither has a real
 * identity to persist, so there's nothing beyond the session flag itself. */
class SignInUseCase @Inject constructor(
    private val appPreferences: AppPreferences,
) {
    suspend operator fun invoke() {
        appPreferences.setLoggedIn(true)
    }
}