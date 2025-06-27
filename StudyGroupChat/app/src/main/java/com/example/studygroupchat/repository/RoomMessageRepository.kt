package com.example.studygroupchat.repository

import com.example.studygroupchat.api.RoomMessageApiService
import com.example.studygroupchat.data.local.MessageDao
import com.example.studygroupchat.data.local.MessageEntity
import com.example.studygroupchat.model.room.RoomMessage
import okhttp3.MultipartBody
import okhttp3.RequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomMessageRepository(
    private val apiService: RoomMessageApiService,
    private val messageDao: MessageDao
) {
    suspend fun getRoomMessages(roomId: String, page: Int? = null, limit: Int? = null): Result<List<RoomMessage>> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getRoomMessages(roomId, page, limit)
                if (response.isSuccessful) {
                    response.body()?.let { messages ->
                        messageDao.clearMessagesForRoom(roomId.toInt())
                        messageDao.insertMessages(messages.map { it.toEntity() })
                        Result.success(messages)
                    } ?: Result.failure(Exception("Empty response body"))
                } else {
                    Result.failure(Exception("Failed to get messages: ${response.code()}"))
                }
            } catch (e: Exception) {
                val cached = messageDao.getMessagesForRoom(roomId.toInt()).map { it.toModel() }
                if (cached.isNotEmpty()) {
                    Result.success(cached)
                } else {
                    Result.failure(e)
                }
            }
        }

    suspend fun sendRoomMessage(roomId: String, content: String): Result<RoomMessage> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.sendRoomMessage(roomId, mapOf("content" to content))
                if (response.isSuccessful) {
                    response.body()?.let { message ->
                        messageDao.insertMessage(message.toEntity())
                        Result.success(message)
                    } ?: Result.failure(Exception("Empty response body"))
                } else {
                    Result.failure(Exception("Failed to send message: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun sendRoomAttachment(
        roomId: String,
        filePart: MultipartBody.Part,
        type: RequestBody
    ): Result<RoomMessage> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.sendRoomAttachment(roomId, filePart, type)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed to send attachment: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCachedMessageCount(): Int = withContext(Dispatchers.IO) {
        messageDao.getMessageCount()
    }

    suspend fun getCachedMessageCountForUser(userId: Int): Int = withContext(Dispatchers.IO) {
        messageDao.getMessageCountForSender(userId)
    }

    suspend fun getLastRoomMessage(roomId: String): Result<RoomMessage?> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getLastMessage(roomId)
            if (response.isSuccessful) {
                response.body()?.let { messageDao.insertMessage(it.toEntity()) }
                Result.success(response.body())
            } else {
                Result.failure(Exception("Failed to get last message: ${response.code()}"))
            }
        } catch (e: Exception) {
            val cached = messageDao.getMessagesForRoom(roomId.toInt()).lastOrNull()?.toModel()
            if (cached != null) Result.success(cached) else Result.failure(e)
        }
    }

    private fun RoomMessage.toEntity() = MessageEntity(
        messageId,
        roomId,
        senderId,
        sender?.fullName ?: sender?.userName,
        sender?.avatarUrl,
        content,
        sentAt,
    )

    private fun MessageEntity.toModel() = RoomMessage(
        messageId = messageId,
        roomId = roomId,
        senderId = senderId,
        content = content,
        sentAt = sentAt,
        sender = if (senderName != null || senderAvatar != null) {
            com.example.studygroupchat.model.user.User(
                senderId,
                "",
                senderName,
                "",
                "",
                null,
                senderAvatar,
                ""
            )
        } else null,
    )
}
