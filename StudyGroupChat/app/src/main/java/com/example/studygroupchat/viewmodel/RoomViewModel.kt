package com.example.studygroupchat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studygroupchat.model.room.Room
import com.example.studygroupchat.repository.RoomRepository
import kotlinx.coroutines.launch

class RoomViewModel(private val repository: RoomRepository) : ViewModel() {
    private val _rooms = MutableLiveData<List<Room>>()
    val rooms: LiveData<List<Room>> = _rooms

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _joinResult = MutableLiveData<Result<Room>>()
    val joinResult: LiveData<Result<Room>> = _joinResult

    private val _selectedRoom = MutableLiveData<Result<Room>>()
    val selectedRoom: LiveData<Result<Room>> = _selectedRoom

    fun fetchMyRooms() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getMyRooms()
            result.onSuccess {
                _rooms.value = it
            }
            _isLoading.value = false
        }
    }

    fun getRoom(roomId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _selectedRoom.value = repository.getRoom(roomId)
            _isLoading.value = false
        }
    }

    fun joinRoom(inviteCode: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.joinRoom(inviteCode)
            _joinResult.value = result
            if (result.isSuccess) {
                // refresh room list after successful join
                fetchMyRooms()
            }
            _isLoading.value = false
        }
    }
}