package com.example.studygroupchat.model.user

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class User(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("user_name")
    val userName: String,
    @SerializedName("full_name")
    val fullName: String?,
    @SerializedName("email")
    val email: String,
    @SerializedName("phone_number")
    val phoneNumber: String,
    @SerializedName("password_hash")
    val passwordHash: String?,
    @SerializedName("avatar_url")
    val avatarUrl: String?,
    @SerializedName("created_at")
    val createdAt: String
): Serializable
data class UpdateUserRequest(
    @SerializedName("full_name")
    val fullName: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("phone_number")
    val phoneNumber: String? = null,
    @SerializedName("avatar_url")
    val avatarUrl: String? = null,
)

data class ChangePasswordRequest(
    @SerializedName("current_password")
    val currentPassword: String,
    @SerializedName("new_password")
    val newPassword: String,
)

//data class User(
//    @SerializedName("userId")
//    val userId: Int,
//    @SerializedName("userName")
//    val userName: String,
//    @SerializedName("fullName")
//    val fullName: String?,
//    @SerializedName("email")
//    val email: String,
//    @SerializedName("phoneNumber")
//    val phoneNumber: String,
//    @SerializedName("avatarUrl")
//    val avatarUrl: String?,
//)


data class LoginRequest(
    val userName: String,
    val password: String
)

data class RegisterRequest(
    val userName: String,
    val email: String,
    val phoneNumber: String,
    val password: String,
    val fullName: String? = null,
    val avatarUrl: String? = null
)

