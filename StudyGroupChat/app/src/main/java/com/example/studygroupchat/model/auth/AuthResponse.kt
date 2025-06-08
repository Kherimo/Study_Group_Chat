package com.example.studygroupchat.model.auth

import com.google.gson.annotations.SerializedName


data class AuthResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
)