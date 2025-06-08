package com.example.studygroupchat.model

import com.example.studygroupchat.model.user.User
import com.google.gson.annotations.SerializedName

data class ChatGroupMember(
    @SerializedName("group_id")
    val groupId: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("joined_at")
    val joinedAt: String,

    @SerializedName("user")
    val user: User? = null
)
