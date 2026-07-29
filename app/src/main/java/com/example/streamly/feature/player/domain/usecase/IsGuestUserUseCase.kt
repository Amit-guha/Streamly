package com.example.streamly.feature.player.domain.usecase

import com.example.streamly.core.common.enum.UserType
import com.example.streamly.core.domain.storage.datastore.AppPreferences
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class IsGuestUserUseCase @Inject constructor(
    private val appPreferences: AppPreferences,
) {
    suspend operator fun invoke(): Boolean = appPreferences.userTypeFlow.first() == UserType.GUEST
}