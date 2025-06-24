package com.example.studygroupchat.api

import com.example.studygroupchat.model.room.CreateRoomRequest
import com.example.studygroupchat.model.room.Room
import com.example.studygroupchat.model.room.RoomMember
import com.example.studygroupchat.model.room.JoinRoomResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface RoomApiService {
    @GET("api/rooms/my-rooms")
    suspend fun getMyRooms(): Response<List<Room>>

    @GET("api/rooms/public")
    suspend fun getPublicRooms(): Response<List<Room>>

    @Multipart
    @POST("api/rooms")
    suspend fun createRoom(
        @Part avatar: MultipartBody.Part?,
        @Part("room_name") roomName: RequestBody,
        @Part("description") description: RequestBody?,
        @Part("room_mode") roomMode: RequestBody?,
        @Part("expired_at") expiredAt: RequestBody?
    ): Response<Room>

    @GET("api/rooms/{roomId}")
    suspend fun getRoom(@Path("roomId") roomId: String): Response<Room>

    @Multipart
    @PUT("api/rooms/{roomId}")
    suspend fun updateRoom(
        @Path("roomId") roomId: String,
        @Part avatar: MultipartBody.Part?,
        @Part("room_name") roomName: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part("room_mode") roomMode: RequestBody?,
        @Part("expired_at") expiredAt: RequestBody?
    ): Response<Room>

    @DELETE("api/rooms/{roomId}")
    suspend fun deleteRoom(@Path("roomId") roomId: String): Response<Unit>

    @POST("api/rooms/join")
    suspend fun joinRoom(@Body request: Map<String, String>): Response<JoinRoomResponse>

    @POST("api/rooms/{roomId}/leave")
    suspend fun leaveRoom(@Path("roomId") roomId: String): Response<Unit>

    @GET("api/rooms/{roomId}/members")
    suspend fun getRoomMembers(@Path("roomId") roomId: String): Response<List<RoomMember>>

    @GET("rooms/{id}")
    suspend fun getRoomById(@Path("id") roomId: Int): Room

}