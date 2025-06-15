package com.example.studygroupchat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studygroupchat.model.room.RoomMessage
import com.example.studygroupchat.repository.RoomMessageRepository
import kotlinx.coroutines.launch

class RoomMessageViewModel(private val repository: RoomMessageRepository) : ViewModel() {
    private val _messages = MutableLiveData<List<RoomMessage>>()
    val messages: LiveData<List<RoomMessage>> = _messages

    private val _sendResult = MutableLiveData<Result<RoomMessage>>()
    val sendResult: LiveData<Result<RoomMessage>> = _sendResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchRoomMessages(roomId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getRoomMessages(roomId)
            result.onSuccess { messages ->
                _messages.value = messages.sortedBy { it.sentAt }
            }
            _isLoading.value = false
        }
    }

    fun sendRoomMessage(roomId: String, content: String) {
        viewModelScope.launch {
            val result = repository.sendRoomMessage(roomId, content)
            _sendResult.value = result
            if (result.isSuccess) {
                // refresh messages
                fetchRoomMessages(roomId)
            }
        }
    }
}