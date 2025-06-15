package com.example.studygroupchat.api

import com.example.studygroupchat.model.room.RoomMessage
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RoomMessageApiService {
    @GET("api/rooms/{roomId}/messages")
    suspend fun getRoomMessages(
        @Path("roomId") roomId: String,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<List<RoomMessage>>

    @POST("api/rooms/{roomId}/messages")
    suspend fun sendRoomMessage(
        @Path("roomId") roomId: String,
        @Body request: Map<String, String>
    ): Response<RoomMessage>
}