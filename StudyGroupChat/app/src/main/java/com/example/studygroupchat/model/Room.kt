package com.example.studygroupchat.model

data class Room(
    val id: String,
    val roomName: String,
    val description: String,
    val ownerName: String,
    val memberCount: Int,
    val isActive: Boolean,
    val showMemberCount: Boolean
)