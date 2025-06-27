package com.example.studygroupchat.repository

import com.example.studygroupchat.api.RoomApiService
import com.example.studygroupchat.data.local.RoomDao
import com.example.studygroupchat.data.local.RoomEntity
import com.example.studygroupchat.model.room.CreateRoomRequest
import com.example.studygroupchat.model.room.Room
import com.example.studygroupchat.model.room.RoomMember
import com.example.studygroupchat.model.user.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class RoomRepository(
    private val apiService: RoomApiService,
    private val roomDao: RoomDao
) {
    suspend fun getMyRooms(): Result<List<Room>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getMyRooms()
            if (response.isSuccessful) {
                response.body()?.let { rooms ->
                    roomDao.clearRooms()
                    roomDao.insertRooms(rooms.map { it.toEntity() })
                    Result.success(rooms)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed to get rooms: ${response.code()}"))
            }
        } catch (e: Exception) {
            val cached = roomDao.getRooms().map { it.toModel() }
            if (cached.isNotEmpty()) {
                Result.success(cached)
            } else {
                Result.failure(e)
            }
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
                response.body()?.room?.let { return@withContext Result.success(it) }
                return@withContext Result.failure(Exception("Empty response body"))
            } else {
                // Some cases return 400 even though the user is already a member
                val error = response.errorBody()?.string()
                if (error?.contains("Already a member") == true) {
                    val myRoomsRes = apiService.getMyRooms()
                    if (myRoomsRes.isSuccessful) {
                        val room = myRoomsRes.body()?.find { it.inviteCode == inviteCode }
                        if (room != null) {
                            return@withContext Result.success(room)
                        }
                    }
                }
                return@withContext Result.failure(Exception("Failed to join room: ${'$'}{response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun leaveRoom(roomId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.leaveRoom(roomId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to leave room: ${'$'}{response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteRoom(roomId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteRoom(roomId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete room: ${'$'}{response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateRoom(
        roomId: String,
        file: MultipartBody.Part?,
        name: RequestBody?,
        description: RequestBody?,
        mode: RequestBody?,
        expiredAt: RequestBody?
    ): Result<Room> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateRoom(
                roomId,
                file,
                name,
                description,
                mode,
                expiredAt
            )
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed to update room: ${'$'}{response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRoomMembers(roomId: String): Result<List<RoomMember>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getRoomMembers(roomId)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed to get members: ${'$'}{response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCachedRoomCount(): Int = withContext(Dispatchers.IO) {
        roomDao.getRoomCount()
    }

    suspend fun getCachedRooms(): List<Room> = withContext(Dispatchers.IO) {
        roomDao.getRooms().map { it.toModel() }
    }
    suspend fun removeMember(roomId: String, userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.removeMember(roomId, userId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to remove member: ${'$'}{response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun Room.toEntity() = RoomEntity(
        roomId,
        ownerId,
        owner?.fullName ?: owner?.userName,
        members?.size,
        roomName,
        roomMode,
        avatarUrl,
        description,
        inviteCode,
        expiredAt,
        createdAt,
    )

    private fun RoomEntity.toModel() = Room(
        roomId,
        ownerId,
        roomName,
        roomMode,
        avatarUrl,
        description,
        inviteCode,
        expiredAt,
        createdAt,
        owner = ownerName?.let {
            User(
                userId = ownerId,
                userName = "",
                fullName = it,
                email = "",
                phoneNumber = "",
                passwordHash = null,
                avatarUrl = null,
                createdAt = ""
            )
        },
        members = memberCount?.let { count ->
            List(count) {
                User(0, "", null, "", "", null, "", "")
            }
        },
        latestMessage = null,
    )
}