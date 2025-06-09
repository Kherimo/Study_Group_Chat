package com.example.studygroupchat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studygroupchat.repository.RoomRepository

class RoomViewModelFactory(private val repository: RoomRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoomViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RoomViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}