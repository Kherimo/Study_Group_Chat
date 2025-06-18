package com.example.studygroupchat.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rooms")
data class RoomEntity(
    @PrimaryKey val roomId: Int,
    val ownerId: Int,
    val ownerName: String?,
    val memberCount: Int?,
    val roomName: String,
    val roomMode: String?,
    val avatarUrl: String?,
    val description: String?,
    val inviteCode: String?,
    val expiredAt: String?,
    val createdAt: String,
)