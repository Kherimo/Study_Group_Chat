package com.example.studygroupchat.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val userId: Int,
    val userName: String,
    val fullName: String?,
    val email: String,
    val phoneNumber: String,
    val avatarUrl: String?,
)