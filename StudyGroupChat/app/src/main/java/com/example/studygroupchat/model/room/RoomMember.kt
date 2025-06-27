package com.example.studygroupchat.model.room

import com.example.studygroupchat.model.user.User
import com.google.gson.annotations.SerializedName

data class RoomMember(
    @SerializedName("room_id")
    val roomId: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("joined_at")
    val joinedAt: String,

    // Tùy theo API có trả thêm thông tin user không
    @SerializedName("users")
    val user: User? = null
)
