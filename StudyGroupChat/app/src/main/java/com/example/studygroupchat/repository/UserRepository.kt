package com.example.studygroupchat.repository

import com.example.studygroupchat.api.UserApiService
import com.example.studygroupchat.model.user.User
import com.example.studygroupchat.model.user.ChangePasswordRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val apiService: UserApiService) {
    suspend fun getCurrentUser(): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCurrentUser()
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed to fetch user: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCurrentUser(
        file: MultipartBody.Part?,
        fullName: RequestBody?,
        email: RequestBody?,
        phone: RequestBody?
    ): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateCurrentUser(file, fullName, email, phone)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed to update user: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changePassword(request: ChangePasswordRequest): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.changePassword(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to change password: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}