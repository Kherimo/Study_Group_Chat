package com.example.studygroupchat.model.room

import com.example.studygroupchat.model.user.User
import com.google.gson.annotations.SerializedName
import java.io.Serializable

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

    // Thông tin người gửi (khi JOIN bảng users)
    @SerializedName("users")
    val sender: User? = null
): Serializable