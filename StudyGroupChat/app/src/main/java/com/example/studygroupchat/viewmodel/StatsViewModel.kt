package com.example.studygroupchat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studygroupchat.repository.RoomMessageRepository
import com.example.studygroupchat.repository.RoomRepository
import com.example.studygroupchat.repository.UserRepository
import kotlinx.coroutines.launch

class StatsViewModel(
    private val userRepository: UserRepository,
    private val roomRepository: RoomRepository,
    private val messageRepository: RoomMessageRepository
) : ViewModel() {
    private val _roomCount = MutableLiveData<Int>()
    val roomCount: LiveData<Int> = _roomCount

    private val _messageCount = MutableLiveData<Int>()
    val messageCount: LiveData<Int> = _messageCount

    fun loadStats() {
        viewModelScope.launch {
            _roomCount.value = roomRepository.getCachedRoomCount()
            val user = userRepository.getCachedUser()
            _messageCount.value = user?.let {
                messageRepository.getCachedMessageCountForUser(it.userId)
            } ?: 0
        }
    }
}