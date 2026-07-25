package com.example.streamly.core.domain.storage.datastore

import kotlinx.coroutines.flow.Flow

interface AppPreferences {
    val isLoggedInFlow: Flow<Boolean>
    val userNameFlow: Flow<String?>
    val userEmailFlow: Flow<String?>

    suspend fun isLoggedIn(): Boolean
    suspend fun setLoggedIn(loggedIn: Boolean)
    suspend fun saveUserProfile(name: String, email: String)
    suspend fun clearSession()
}