package com.example.studygroupchat.model.room

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RoomHistory(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("room_id")
    val roomId: Int,
    @SerializedName("joined_at")
    val joinedAt: String,
    @SerializedName("left_at")
    val leftAt: String?
): Serializable
