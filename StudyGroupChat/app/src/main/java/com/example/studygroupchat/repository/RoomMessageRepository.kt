package com.example.studygroupchat.repository

import com.example.studygroupchat.api.RoomMessageApiService
import com.example.studygroupchat.model.room.RoomMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomMessageRepository(private val apiService: RoomMessageApiService) {
    suspend fun getRoomMessages(roomId: String, page: Int? = null, limit: Int? = null): Result<List<RoomMessage>> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getRoomMessages(roomId, page, limit)
                if (response.isSuccessful) {
                    response.body()?.let { Result.success(it) }
                        ?: Result.failure(Exception("Empty response body"))
                } else {
                    Result.failure(Exception("Failed to get messages: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun sendRoomMessage(roomId: String, content: String): Result<RoomMessage> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.sendRoomMessage(roomId, mapOf("content" to content))
                if (response.isSuccessful) {
                    response.body()?.let { Result.success(it) }
                        ?: Result.failure(Exception("Empty response body"))
                } else {
                    Result.failure(Exception("Failed to send message: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getLastRoomMessage(roomId: String): Result<RoomMessage?> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getLastMessage(roomId)
            if (response.isSuccessful) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Failed to get last message: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}