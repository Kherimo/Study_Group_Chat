package com.example.studygroupchat.model

data class Group(
    val id: String,
    val name: String,
    val lastMessage: String,
    val avatarUrl: String?,
    val lastMessageTime: String?,
    val lastMessageTimestamp: Long? = null,
    val roomMode: String? = null,
    val memberCount: Int = 0
)