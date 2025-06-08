package com.example.studygroupchat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studygroupchat.api.RoomApiService

class CreateRoomViewModelFactory(private val roomApiService: RoomApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateRoomViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateRoomViewModel(roomApiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 