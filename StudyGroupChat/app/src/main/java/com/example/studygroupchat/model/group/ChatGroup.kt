package com.example.studygroupchat.model

import com.example.studygroupchat.model.user.User
import com.google.gson.annotations.SerializedName

data class ChatGroup(
    @SerializedName("group_id")
    val groupId: Int,
    @SerializedName("group_name")
    val groupName: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("created_by")
    val createdBy: Int,
    @SerializedName("group_chat_code")
    val groupChatCode: String?,

    // Optional: người tạo group
    @SerializedName("creator")
    val creator: User? = null
)
