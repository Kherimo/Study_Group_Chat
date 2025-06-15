package com.example.studygroupchat.repository

import com.example.studygroupchat.api.RoomApiService
import com.example.studygroupchat.model.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomRepository(private val apiService: RoomApiService) {
    suspend fun getMyRooms(): Result<List<Room>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getMyRooms()
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed to get rooms: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPublicRooms(): Result<List<Room>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getPublicRooms()
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed to get rooms: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRoom(roomId: String): Result<Room> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getRoom(roomId)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed to get room: ${'$'}{response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun joinRoom(inviteCode: String): Result<Room> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.joinRoom(mapOf("invite_code" to inviteCode))
            if (response.isSuccessful) {
                response.body()?.room?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed to join room: ${'$'}{response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}