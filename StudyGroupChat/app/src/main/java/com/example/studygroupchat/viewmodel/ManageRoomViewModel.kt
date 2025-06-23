package com.example.studygroupchat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studygroupchat.model.room.Room
import com.example.studygroupchat.repository.RoomRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class ManageRoomViewModel(private val repository: RoomRepository) : ViewModel() {
    private val _room = MutableLiveData<Room>()
    val room: LiveData<Room> = _room

    private val _updateResult = MutableLiveData<Result<Room>>()
    val updateResult: LiveData<Result<Room>> = _updateResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchRoom(roomId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getRoom(roomId)
            result.onSuccess { _room.value = it }
            _isLoading.value = false
        }
    }

    fun updateRoom(
        roomId: String,
        file: MultipartBody.Part?,
        name: String?,
        description: String?,
        mode: String?,
        expiredAt: String?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.updateRoom(
                roomId,
                file,
                name?.toRequestBody("text/plain".toMediaTypeOrNull()),
                description?.toRequestBody("text/plain".toMediaTypeOrNull()),
                mode?.toRequestBody("text/plain".toMediaTypeOrNull()),
                expiredAt?.toRequestBody("text/plain".toMediaTypeOrNull())
            )
            result.onSuccess { _room.value = it }
            _updateResult.value = result
            _isLoading.value = false
        }
    }
}