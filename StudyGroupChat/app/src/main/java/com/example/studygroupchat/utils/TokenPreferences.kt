package com.example.studygroupchat.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token_preferences")

class TokenPreferences(private val context: Context) {

    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
            preferences[REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun getAccessToken(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN]
        }.first()
    }

    suspend fun getRefreshToken(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN]
        }.first()
    }

    val tokensFlow: Flow<TokenData> = context.dataStore.data.map { preferences ->
        TokenData(
            accessToken = preferences[ACCESS_TOKEN] ?: "",
            refreshToken = preferences[REFRESH_TOKEN] ?: ""
        )
    }

    suspend fun clearTokens() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

data class TokenData(
    val accessToken: String,
    val refreshToken: String
)
