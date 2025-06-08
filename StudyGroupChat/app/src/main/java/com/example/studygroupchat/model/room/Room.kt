package com.example.studygroupchat.model.room

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.studygroupchat.model.user.User
import com.google.gson.annotations.SerializedName

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Room(
    @SerializedName("room_id")
    val roomId: Int,

    @SerializedName("owner_id")
    val ownerId: Int,

    @SerializedName("room_name")
    val roomName: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("invite_code")
    val inviteCode: String?,

    @SerializedName("expired_at")
    val expiredAt: String?,  // ISO 8601 format or null

    @SerializedName("created_at")
    val createdAt: String,

    // JOIN từ bảng users
    @SerializedName("owner")
    val owner: User?,

    // JOIN từ bảng room_members (nếu API cung cấp)
    @SerializedName("members")
    val members: List<User>? = null
) {
    // ✅ Tính toán trạng thái hoạt động
    val isActive: Boolean
        @RequiresApi(Build.VERSION_CODES.O)
        get() {
            if (expiredAt.isNullOrBlank()) return true
            return try {
                val formatter = DateTimeFormatter.ISO_DATE_TIME
                val expiry = LocalDateTime.parse(expiredAt, formatter)
                expiry.isAfter(LocalDateTime.now())
            } catch (e: Exception) {
                true // Nếu lỗi parse, coi như đang hoạt động
            }
        }
}


//data class Room(
//    @SerializedName("room_id")
//    val roomId: String,
//    @SerializedName("room_name")
//    val roomName: String,
//    @SerializedName("description")
//    val description: String?,
//    @SerializedName("owner_id")
//    val ownerId: String,
//    @SerializedName("invite_code")
//    val inviteCode: String,
//    @SerializedName("expired_at")
//    val expiredAt: String?,
//    @SerializedName("users")
//    val owner: User?,
//    @SerializedName("is_active")
//    val isActive: Boolean = true,
//    @SerializedName("member_count")
//    val memberCount: Int = 0,
//    @SerializedName("show_member_count")
//    val showMemberCount: Boolean = true
//)
//
//data class RoomMember(
//    @SerializedName("user_id")
//    val userId: String,
//    @SerializedName("user_name")
//    val userName: String,
//    @SerializedName("full_name")
//    val fullName: String,
//    @SerializedName("avatar_url")
//    val avatarUrl: String?
//)