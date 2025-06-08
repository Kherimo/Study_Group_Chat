package com.example.studygroupchat.model.room

import com.google.gson.annotations.SerializedName

data class CreateRoomRequest(
    @SerializedName("room_name")
    val roomName: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("expired_at")
    val expiredAt: String?
) 