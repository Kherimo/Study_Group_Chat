package com.example.studygroupchat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.studygroupchat.api.ApiConfig
import com.example.studygroupchat.model.LoginRequest
import com.example.studygroupchat.model.RegisterRequest
import com.example.studygroupchat.model.AuthResponse
import com.example.studygroupchat.repository.AuthRepository
import com.example.studygroupchat.utils.UserPreferences
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository(ApiConfig.authApiService)
    private val userPreferences = UserPreferences(application)

    private val _loginResult = MutableLiveData<Result<AuthResponse>>()
    val loginResult: LiveData<Result<AuthResponse>> = _loginResult

    private val _registerResult = MutableLiveData<Result<AuthResponse>>()
    val registerResult: LiveData<Result<AuthResponse>> = _registerResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(userName: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.login(LoginRequest(userName, password))
                result.onSuccess { response ->
                    // Save user data to preferences
                    userPreferences.saveUserData(
                        userId = response.user.userId,
                        userName = response.user.userName,
                        fullName = response.user.fullName,
                        email = response.user.email,
                        phoneNumber = response.user.phoneNumber,
                        avatarUrl = response.user.avatarUrl,
                        accessToken = response.accessToken,
                        refreshToken = response.refreshToken
                    )
                }
                _loginResult.value = result
            } catch (e: Exception) {
                _loginResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(
        userName: String,
        email: String,
        phoneNumber: String,
        password: String,
        fullName: String? = null,
        avatarUrl: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.register(
                    RegisterRequest(
                        userName = userName,
                        email = email,
                        phoneNumber = phoneNumber,
                        password = password,
                        fullName = fullName,
                        avatarUrl = avatarUrl
                    )
                )
                result.onSuccess { response ->
                    // Save user data to preferences
                    userPreferences.saveUserData(
                        userId = response.user.userId,
                        userName = response.user.userName,
                        fullName = response.user.fullName,
                        email = response.user.email,
                        phoneNumber = response.user.phoneNumber,
                        avatarUrl = response.user.avatarUrl,
                        accessToken = response.accessToken,
                        refreshToken = response.refreshToken
                    )
                }
                _registerResult.value = result
            } catch (e: Exception) {
                _registerResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.clearUserData()
        }
    }

    fun getUserData() = userPreferences.userData
} 