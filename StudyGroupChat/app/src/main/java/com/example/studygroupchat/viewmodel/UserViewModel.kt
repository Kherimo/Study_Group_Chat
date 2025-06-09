package com.example.studygroupchat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studygroupchat.model.user.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.example.studygroupchat.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    fun fetchCurrentUser() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getCurrentUser()
            result.onSuccess { _user.value = it }
            _isLoading.value = false
        }
    }

}