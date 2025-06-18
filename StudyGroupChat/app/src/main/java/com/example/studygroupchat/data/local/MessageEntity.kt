package com.example.studygroupchat.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val messageId: Int,
    val roomId: Int,
    val senderId: Int,
    val senderName: String?,
    val senderAvatar: String?,
    val content: String?,
    val sentAt: String,
)