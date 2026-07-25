package com.example.streamly.core.data.storage.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.streamly.core.common.constant.DataStoreConstants
import com.example.streamly.core.domain.storage.datastore.AppPreferences
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AppPreferencesImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : AppPreferences {

    private val isLoggedInKey = booleanPreferencesKey(DataStoreConstants.KEY_IS_LOGGED_IN)

    override val isLoggedInFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[isLoggedInKey] ?: false
    }

    override suspend fun isLoggedIn(): Boolean = isLoggedInFlow.first()

    override suspend fun setLoggedIn(loggedIn: Boolean) {
        dataStore.edit { preferences -> preferences[isLoggedInKey] = loggedIn }
    }

    override suspend fun clearSession() {
        dataStore.edit { preferences -> preferences.clear() }
    }
}