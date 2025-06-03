package com.example.studygroupchat.model

data class User(
    val userId: String,
    val userName: String,
    val fullName: String?,
    val email: String,
    val phoneNumber: String,
    val avatarUrl: String?
)

data class LoginRequest(
    val userName: String,
    val password: String
)

data class RegisterRequest(
    val userName: String,
    val email: String,
    val phoneNumber: String,
    val password: String,
    val fullName: String? = null,
    val avatarUrl: String? = null
)

data class AuthResponse(
    val message: String,
    val user: User,
    val accessToken: String,
    val refreshToken: String
) 