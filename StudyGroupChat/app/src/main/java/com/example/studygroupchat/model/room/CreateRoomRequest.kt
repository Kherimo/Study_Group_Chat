package com.example.studygroupchat.model.room

import com.google.gson.annotations.SerializedName

data class CreateRoomRequest(
    @SerializedName("room_name")
    val roomName: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("room_mode")
    val roomMode: String? = null,
    @SerializedName("avatar_url")
    val avatarUrl: String? = null,
    @SerializedName("expired_at")
    val expiredAt: String?,
)