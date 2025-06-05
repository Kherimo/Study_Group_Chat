package com.example.studygroupchat.model

data class RoomWithStatus(
    val room: Room,
    val isJoined: Boolean // true = visited, false = gone
)
