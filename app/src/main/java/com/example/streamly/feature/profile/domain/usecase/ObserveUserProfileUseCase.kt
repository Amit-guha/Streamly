package com.example.streamly.feature.profile.domain.usecase

import com.example.streamly.core.domain.storage.datastore.AppPreferences
import com.example.streamly.feature.profile.domain.model.UserProfile
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveUserProfileUseCase @Inject constructor(
    private val appPreferences: AppPreferences,
) {
    operator fun invoke(): Flow<UserProfile> =
        appPreferences.userNameFlow.combine(appPreferences.userEmailFlow) { name, email ->
            UserProfile(name = name, email = email)
        }
}