package com.example.studygroupchat.api

import com.example.studygroupchat.model.AuthResponse
import com.example.studygroupchat.model.LoginRequest
import com.example.studygroupchat.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("authen/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("authen/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
} 