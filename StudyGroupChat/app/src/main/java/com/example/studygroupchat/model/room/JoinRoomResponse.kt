package com.example.studygroupchat.model.room

import com.google.gson.annotations.SerializedName

data class JoinRoomResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("room")
    val room: Room
)