package com.example.studygroupchat.model.room

import com.example.studygroupchat.model.user.User
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RoomMessage(
    @SerializedName("message_id")
    val messageId: Int,
    @SerializedName("room_id")
    val roomId: Int,
    @SerializedName("sender_id")
    val senderId: Int,
    @SerializedName("content")
    val content: String?,

    // URL file đính kèm (nếu có)
    @SerializedName("file_url")
    val fileUrl: String? = null,

    // Tên file đính kèm
    @SerializedName("file_name")
    val fileName: String? = null,

    // Kích thước file
    @SerializedName("file_size")
    val fileSize: Long? = null,

    // Kiểu tin nhắn: text, image, video, file
    @SerializedName("message_type")
    val messageType: String? = "text",
    @SerializedName("sent_at")
    val sentAt: String,

    // Thông tin người gửi (khi JOIN bảng users)
    @SerializedName("users")
    val sender: User? = null
) : Serializable