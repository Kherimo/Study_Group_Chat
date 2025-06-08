 package com.example.studygroupchat.model.auth

import com.google.gson.annotations.SerializedName

data class RefreshTokenRequest(
    @SerializedName("refresh_token")
    val refreshToken: String
)