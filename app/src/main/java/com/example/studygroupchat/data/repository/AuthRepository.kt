package com.example.studygroupchat.data.repository

import com.example.studygroupchat.data.api.AuthApi
import com.example.studygroupchat.data.model.*
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: AuthApi
) {
    suspend fun login(email: String, password: String): LoginResponse {
        return api.login(LoginRequest(email, password))
    }

    suspend fun register(name: String, email: String, password: String): BasicResponse {
        return api.register(RegisterRequest(email, password, name))
    }
}
