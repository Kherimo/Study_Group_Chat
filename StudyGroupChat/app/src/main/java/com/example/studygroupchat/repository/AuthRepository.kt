package com.example.studygroupchat.repository

import com.example.studygroupchat.api.AuthApiService
import com.example.studygroupchat.model.auth.AccessTokenResponse
import com.example.studygroupchat.model.auth.AuthResponse
import com.example.studygroupchat.model.user.LoginRequest
import com.example.studygroupchat.model.auth.RefreshTokenRequest
import com.example.studygroupchat.model.user.RegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val apiService: AuthApiService) {
    suspend fun login(request: LoginRequest): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.login(request)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Login failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(request: RegisterRequest): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.register(request)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Registration failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshToken(refreshToken: String): Result<AccessTokenResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.refreshToken(RefreshTokenRequest(refreshToken))
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Refresh failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



} 