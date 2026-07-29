package com.example.streamly.feature.auth.authentication.domain.usecase

import com.example.streamly.core.common.constant.AppConstants
import com.example.streamly.core.common.enum.UserType
import com.example.streamly.core.domain.storage.datastore.AppPreferences
import javax.inject.Inject

/** Marks the session as signed in for the Google and guest paths. Guest has no identity to
 * persist; Google isn't a real integration yet, so it's given a fixed placeholder identity. */
class SignInUseCase @Inject constructor(
    private val appPreferences: AppPreferences,
) {
    suspend operator fun invoke(userType: UserType) {
        if (userType == UserType.GOOGLE) {
            appPreferences.saveUserProfile(
                name = AppConstants.GOOGLE_USER_NAME,
                email = AppConstants.GOOGLE_USER_EMAIL,
            )
        }
        appPreferences.setUserType(userType)
        appPreferences.setLoggedIn(true)
    }
}