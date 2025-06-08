package com.example.studygroupchat.model

import com.example.studygroupchat.model.user.User
import com.google.gson.annotations.SerializedName

data class ChatGroupMessage(
    @SerializedName("message_id")
    val messageId: Int,
    @SerializedName("group_id")
    val groupId: Int,
    @SerializedName("sender_id")
    val senderId: Int,
    @SerializedName("content_text")
    val contentText: String?,
    @SerializedName("content_file_url")
    val contentFileUrl: String?,
    @SerializedName("sent_at")
    val sentAt: String,

    @SerializedName("sender")
    val sender: User? = null
)
