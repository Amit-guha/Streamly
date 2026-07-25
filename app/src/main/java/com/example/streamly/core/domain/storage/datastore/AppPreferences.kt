package com.example.streamly.core.domain.storage.datastore

import kotlinx.coroutines.flow.Flow

interface AppPreferences {
    val isLoggedInFlow: Flow<Boolean>
    suspend fun isLoggedIn(): Boolean
    suspend fun setLoggedIn(loggedIn: Boolean)
    suspend fun clearSession()
}