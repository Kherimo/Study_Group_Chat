package com.example.studygroupchat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studygroupchat.api.RoomApiService
import com.example.studygroupchat.model.room.CreateRoomRequest
import com.example.studygroupchat.model.room.Room
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CreateRoomViewModel(private val roomApiService: RoomApiService) : ViewModel() {
    private val _createRoomResult = MutableLiveData<Result<Room>>()
    val createRoomResult: LiveData<Result<Room>> = _createRoomResult

    fun createRoom(roomName: String, description: String?, expiredAt: String?) {
        viewModelScope.launch {
            try {
                val request = CreateRoomRequest(
                    roomName = roomName,
                    description = description,
                    expiredAt = expiredAt
                )
                val response = roomApiService.createRoom(request)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _createRoomResult.value = Result.success(it)
                    } ?: run {
                        _createRoomResult.value = Result.failure(Exception("Empty response body"))
                    }
                } else {
                    _createRoomResult.value = Result.failure(Exception("Failed to create room: ${response.code()}"))
                }
            } catch (e: Exception) {
                _createRoomResult.value = Result.failure(e)
            }
        }
    }

    fun formatDate(date: Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        return dateFormat.format(date)
    }
}