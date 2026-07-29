package com.example.streamly.core.data.storage.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.streamly.core.common.constant.DataStoreConstants
import com.example.streamly.core.common.enum.UserType
import com.example.streamly.core.domain.storage.datastore.AppPreferences
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AppPreferencesImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : AppPreferences {

    private val isLoggedInKey = booleanPreferencesKey(DataStoreConstants.KEY_IS_LOGGED_IN)
    private val userNameKey = stringPreferencesKey(DataStoreConstants.KEY_USER_NAME)
    private val userEmailKey = stringPreferencesKey(DataStoreConstants.KEY_USER_EMAIL)
    private val userTypeKey = stringPreferencesKey(DataStoreConstants.KEY_USER_TYPE)

    override val isLoggedInFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[isLoggedInKey] ?: false
    }

    override val userNameFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[userNameKey]
    }

    override val userEmailFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[userEmailKey]
    }

    override val userTypeFlow: Flow<UserType?> = dataStore.data.map { preferences ->
        preferences[userTypeKey]?.let { UserType.valueOf(it) }
    }

    override suspend fun isLoggedIn(): Boolean = isLoggedInFlow.first()

    override suspend fun setLoggedIn(loggedIn: Boolean) {
        dataStore.edit { preferences -> preferences[isLoggedInKey] = loggedIn }
    }

    override suspend fun saveUserProfile(name: String, email: String) {
        dataStore.edit { preferences ->
            preferences[userNameKey] = name
            preferences[userEmailKey] = email
        }
    }

    override suspend fun setUserType(userType: UserType) {
        dataStore.edit { preferences -> preferences[userTypeKey] = userType.name }
    }

    override suspend fun clearSession() {
        dataStore.edit { preferences -> preferences.clear() }
    }
}
