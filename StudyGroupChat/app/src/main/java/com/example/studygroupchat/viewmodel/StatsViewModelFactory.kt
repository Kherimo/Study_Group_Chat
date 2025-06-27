package com.example.studygroupchat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studygroupchat.repository.RoomMessageRepository
import com.example.studygroupchat.repository.RoomRepository
import com.example.studygroupchat.repository.UserRepository

class StatsViewModelFactory(
    private val userRepository: UserRepository,
    private val roomRepository: RoomRepository,
    private val messageRepository: RoomMessageRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatsViewModel(userRepository, roomRepository, messageRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}