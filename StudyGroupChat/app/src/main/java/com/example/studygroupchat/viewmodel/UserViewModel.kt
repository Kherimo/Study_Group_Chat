package com.example.studygroupchat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studygroupchat.model.user.User
import com.example.studygroupchat.model.user.ChangePasswordRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import com.example.studygroupchat.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _updateResult = MutableLiveData<Result<User>>()
    val updateResult: LiveData<Result<User>> = _updateResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _passwordResult = MutableLiveData<Result<Unit>>()
    val passwordResult: LiveData<Result<Unit>> = _passwordResult


    fun fetchCurrentUser() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getCurrentUser()
            result.onSuccess { _user.value = it }
            _isLoading.value = false
        }
    }

    fun updateCurrentUser(
        file: MultipartBody.Part?,
        fullName: String?,
        email: String?,
        phone: String?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.updateCurrentUser(
                file,
                fullName?.toRequestBody("text/plain".toMediaTypeOrNull()),
                email?.toRequestBody("text/plain".toMediaTypeOrNull()),
                phone?.toRequestBody("text/plain".toMediaTypeOrNull())
            )
            result.onSuccess { _user.value = it }
            _updateResult.value = result
            _isLoading.value = false
        }
    }

    fun changePassword(current: String, newPass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.changePassword(ChangePasswordRequest(current, newPass))
            _passwordResult.value = result
            _isLoading.value = false
        }
    }

}