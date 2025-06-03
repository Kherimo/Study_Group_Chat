package com.example.studygroupchat.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {
    
    companion object {
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val FULL_NAME = stringPreferencesKey("full_name")
        private val EMAIL = stringPreferencesKey("email")
        private val PHONE_NUMBER = stringPreferencesKey("phone_number")
        private val AVATAR_URL = stringPreferencesKey("avatar_url")
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    suspend fun saveUserData(
        userId: String,
        userName: String,
        fullName: String?,
        email: String,
        phoneNumber: String,
        avatarUrl: String?,
        accessToken: String,
        refreshToken: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = userId
            preferences[USER_NAME] = userName
            preferences[FULL_NAME] = fullName ?: ""
            preferences[EMAIL] = email
            preferences[PHONE_NUMBER] = phoneNumber
            preferences[AVATAR_URL] = avatarUrl ?: ""
            preferences[ACCESS_TOKEN] = accessToken
            preferences[REFRESH_TOKEN] = refreshToken
        }
    }

    val userData: Flow<UserData> = context.dataStore.data.map { preferences ->
        UserData(
            userId = preferences[USER_ID] ?: "",
            userName = preferences[USER_NAME] ?: "",
            fullName = preferences[FULL_NAME] ?: "",
            email = preferences[EMAIL] ?: "",
            phoneNumber = preferences[PHONE_NUMBER] ?: "",
            avatarUrl = preferences[AVATAR_URL] ?: "",
            accessToken = preferences[ACCESS_TOKEN] ?: "",
            refreshToken = preferences[REFRESH_TOKEN] ?: ""
        )
    }

    suspend fun clearUserData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

data class UserData(
    val userId: String,
    val userName: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val avatarUrl: String,
    val accessToken: String,
    val refreshToken: String
) 