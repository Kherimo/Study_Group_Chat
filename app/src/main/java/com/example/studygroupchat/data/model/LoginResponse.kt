package com.example.studygroupchat.data.model

data class User(
    val email: String,
    val full_name: String
)

data class LoginResponse(
    val token: String,
    val user: User
)
