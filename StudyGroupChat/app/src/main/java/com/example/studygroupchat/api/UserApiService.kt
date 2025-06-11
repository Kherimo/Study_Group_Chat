package com.example.studygroupchat.api

import com.example.studygroupchat.model.user.User
import com.example.studygroupchat.model.user.ChangePasswordRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Body


interface UserApiService {
    @GET("api/users/me")
    suspend fun getCurrentUser(): Response<User>

    @Multipart
    @PUT("api/users/me")
    suspend fun updateCurrentUser(
        @Part avatar: MultipartBody.Part?,
        @Part("full_name") fullName: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("phone_number") phone: RequestBody?
    ): Response<User>

    @PUT("api/users/me/password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<Unit>

}