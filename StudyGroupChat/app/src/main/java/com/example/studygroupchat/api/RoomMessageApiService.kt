package com.example.studygroupchat.api

import com.example.studygroupchat.model.room.RoomMessage
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface RoomMessageApiService {
    @GET("api/rooms/{roomId}/messages")
    suspend fun getRoomMessages(
        @Path("roomId") roomId: String,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<List<RoomMessage>>

    @GET("api/rooms/{roomId}/messages/latest")
    suspend fun getLastMessage(
        @Path("roomId") roomId: String
    ): Response<RoomMessage>

    @POST("api/rooms/{roomId}/messages")
    suspend fun sendRoomMessage(
        @Path("roomId") roomId: String,
        @Body request: Map<String, String>
    ): Response<RoomMessage>

    @Multipart
    @POST("api/rooms/{roomId}/messages/attachments")
    suspend fun sendRoomAttachment(
        @Path("roomId") roomId: String,
        @Part file: MultipartBody.Part,
        @Part("type") type: RequestBody
    ): Response<RoomMessage>
}