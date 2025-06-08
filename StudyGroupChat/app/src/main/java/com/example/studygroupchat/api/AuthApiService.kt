package com.example.studygroupchat.api

import com.example.studygroupchat.model.auth.AccessTokenResponse
import com.example.studygroupchat.model.auth.AuthResponse
import com.example.studygroupchat.model.user.LoginRequest
import com.example.studygroupchat.model.user.RegisterRequest
import com.example.studygroupchat.model.auth.RefreshTokenRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("authen/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("authen/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("autho/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<AccessTokenResponse>


} 