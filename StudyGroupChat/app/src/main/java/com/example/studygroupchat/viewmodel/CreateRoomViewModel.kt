package com.example.studygroupchat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studygroupchat.api.RoomApiService
import com.example.studygroupchat.model.room.Room
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class CreateRoomViewModel(private val roomApiService: RoomApiService) : ViewModel() {
    private val _createRoomResult = MutableLiveData<Result<Room>>()
    val createRoomResult: LiveData<Result<Room>> = _createRoomResult

    fun createRoom(
        file: MultipartBody.Part?,
        roomName: String,
        description: String?,
        roomMode: String,
        expiredAt: String?
    ) {
        viewModelScope.launch {
            try {
                val response = roomApiService.createRoom(
                    file,
                    roomName.toRequestBody("text/plain".toMediaTypeOrNull()),
                    description?.toRequestBody("text/plain".toMediaTypeOrNull()),
                    roomMode.toRequestBody("text/plain".toMediaTypeOrNull()),
                    expiredAt?.toRequestBody("text/plain".toMediaTypeOrNull())
                )
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