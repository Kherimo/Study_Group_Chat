package com.example.studygroupchat.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataSyncManager(
    private val userRepository: UserRepository,
    private val roomRepository: RoomRepository,
    private val messageRepository: RoomMessageRepository,
) {
    suspend fun syncAll() = withContext(Dispatchers.IO) {
        userRepository.getCurrentUser()
        val rooms = roomRepository.getMyRooms().getOrNull() ?: emptyList()
        for (room in rooms) {
            messageRepository.getRoomMessages(room.roomId.toString())
        }
    }
}