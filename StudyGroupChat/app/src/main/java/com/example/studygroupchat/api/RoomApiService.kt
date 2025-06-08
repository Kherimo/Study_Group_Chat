package com.example.studygroupchat.api

import com.example.studygroupchat.model.room.CreateRoomRequest
import com.example.studygroupchat.model.room.Room
import com.example.studygroupchat.model.room.RoomMember
import retrofit2.Response
import retrofit2.http.*

interface RoomApiService {
    @GET("api/rooms/my-rooms")
    suspend fun getMyRooms(): Response<List<Room>>

    @POST("api/rooms")
    suspend fun createRoom(@Body request: CreateRoomRequest): Response<Room>

    @GET("api/rooms/{roomId}")
    suspend fun getRoom(@Path("roomId") roomId: String): Response<Room>

    @PUT("api/rooms/{roomId}")
    suspend fun updateRoom(@Path("roomId") roomId: String, @Body request: CreateRoomRequest): Response<Room>

    @DELETE("api/rooms/{roomId}")
    suspend fun deleteRoom(@Path("roomId") roomId: String): Response<Unit>

    @POST("api/rooms/join")
    suspend fun joinRoom(@Body request: Map<String, String>): Response<Room>

    @GET("api/rooms/{roomId}/members")
    suspend fun getRoomMembers(@Path("roomId") roomId: String): Response<List<RoomMember>>
}