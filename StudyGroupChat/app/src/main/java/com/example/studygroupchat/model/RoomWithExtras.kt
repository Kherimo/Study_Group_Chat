package com.example.studygroupchat.model


data class RoomWithExtras(
    val room: Room,
    val ownerName: String,
    val memberCount: Int,
    val isVisible: Boolean = true // UI flag
)
