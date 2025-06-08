package com.example.studygroupchat.model.room

import com.example.studygroupchat.model.user.User
import com.google.gson.annotations.SerializedName

data class RoomMessage(
    @SerializedName("message_id")
    val messageId: Int,
    @SerializedName("room_id")
    val roomId: Int,
    @SerializedName("sender_id")
    val senderId: Int,
    @SerializedName("content")
    val content: String?,
    @SerializedName("sent_at")
    val sentAt: String,

    // Optional: dùng khi JOIN bảng users
    @SerializedName("sender")
    val sender: User? = null
)
