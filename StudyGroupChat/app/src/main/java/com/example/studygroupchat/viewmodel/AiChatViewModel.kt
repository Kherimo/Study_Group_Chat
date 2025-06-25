package com.example.studygroupchat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studygroupchat.model.Message
import com.example.studygroupchat.repository.AiChatRepository
import kotlinx.coroutines.launch

class AiChatViewModel(private val repository: AiChatRepository) : ViewModel() {
    private val _messages = MutableLiveData<List<Message>>(emptyList())
    val messages: LiveData<List<Message>> = _messages
    private val _error = MutableLiveData<Throwable?>()
    val error: LiveData<Throwable?> = _error

    fun sendQuestion(question: String) {
        val current = _messages.value?.toMutableList() ?: mutableListOf()
        current.add(Message("user", question))
        _messages.value = current
        viewModelScope.launch {
            val result = repository.ask(question)
            result.onSuccess { answer ->
                val list = _messages.value?.toMutableList() ?: mutableListOf()
                list.add(Message("ai", answer))
                _messages.postValue(list)
            }.onFailure { e ->
                _error.postValue(e)
            }
        }
    }

    fun clearMessages() {
        _messages.value = emptyList()
    }
}
