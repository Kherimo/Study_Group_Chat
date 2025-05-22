package com.example.studygroupchat.data.api

import com.example.studygroupchat.data.model.BasicResponse
import com.example.studygroupchat.data.model.LoginRequest
import com.example.studygroupchat.data.model.LoginResponse
import com.example.studygroupchat.data.model.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): BasicResponse
}
