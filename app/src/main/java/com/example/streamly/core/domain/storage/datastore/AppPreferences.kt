package com.example.streamly.core.domain.storage.datastore

import com.example.streamly.core.common.enum.UserType
import kotlinx.coroutines.flow.Flow

interface AppPreferences {
    val isLoggedInFlow: Flow<Boolean>
    val userNameFlow: Flow<String?>
    val userEmailFlow: Flow<String?>
    val userTypeFlow: Flow<UserType?>

    suspend fun isLoggedIn(): Boolean
    suspend fun setLoggedIn(loggedIn: Boolean)
    suspend fun saveUserProfile(name: String, email: String)
    suspend fun setUserType(userType: UserType)
    suspend fun clearSession()
}