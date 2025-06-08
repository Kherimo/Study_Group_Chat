package com.example.studygroupchat.viewmodel

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.studygroupchat.api.ApiConfig
import com.example.studygroupchat.model.user.LoginRequest
import com.example.studygroupchat.model.user.RegisterRequest
import com.example.studygroupchat.repository.AuthRepository
import com.example.studygroupchat.ui.SplashActivity
import com.example.studygroupchat.utils.TokenPreferences
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository(ApiConfig.authApiService)
    private val tokenPreferences = TokenPreferences(application)

    private val _loginResult = MutableLiveData<Result<Unit>>()
    val loginResult: LiveData<Result<Unit>> = _loginResult

    private val _registerResult = MutableLiveData<Result<Unit>>()
    val registerResult: LiveData<Result<Unit>> = _registerResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(userName: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.login(LoginRequest(userName, password))
                result.onSuccess { authResponse ->
                    tokenPreferences.saveTokens(
                        accessToken = authResponse.accessToken,
                        refreshToken = authResponse.refreshToken
                    )
                    Log.d("AuthViewModel", "Login successful:")
                    Log.d("AuthViewModel", "Access Token: ${authResponse.accessToken}")
                    Log.d("AuthViewModel", "Refresh Token: ${authResponse.refreshToken}")

                    _loginResult.value = Result.success(Unit)
                }
                result.onFailure {
                    _loginResult.value = Result.failure(it)
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Login error: ${e.message}", e)
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
                result.onSuccess { authResponse ->
                    tokenPreferences.saveTokens(
                        accessToken = authResponse.accessToken,
                        refreshToken = authResponse.refreshToken
                    )
                    Log.d("AuthViewModel", "Register successful:")
                    Log.d("AuthViewModel", "Access Token: ${authResponse.accessToken}")
                    Log.d("AuthViewModel", "Refresh Token: ${authResponse.refreshToken}")

                    _registerResult.value = Result.success(Unit)
                }
                result.onFailure {
                    _registerResult.value = Result.failure(it)
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Register error: ${e.message}", e)
                _registerResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
//        viewModelScope.launch {
//            tokenPreferences.clearTokens()
//        }
        viewModelScope.launch {
            tokenPreferences.clearTokens()

            // Chuyển về SplashActivity sau khi logout
            val context = getApplication<Application>().applicationContext
            val intent = Intent(context, SplashActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
        }

    }

//    fun startAutoRefreshToken() {
//        viewModelScope.launch {
//            while (true) {
//                kotlinx.coroutines.delay(1 * 60 * 1000L) // 1 phút
//
//                try {
//                    val refreshToken = tokenPreferences.getRefreshToken()
//                    if (!refreshToken.isNullOrEmpty()) {
//                        val response = repository.refreshToken(refreshToken)
//                        response.onSuccess { result ->
//                            tokenPreferences.saveTokens(
//                                accessToken = result.accessToken,
//                                refreshToken = refreshToken
//                            )
//                            Log.d("TokenRefresh", "Access token refreshed!")
//                        }
//                        response.onFailure {
//                            Log.e("TokenRefresh", "Failed to refresh token: ${it.message}")
//                        }
//                    }
//                } catch (e: Exception) {
//                    Log.e("TokenRefresh", "Exception during token refresh", e)
//                }
//            }
//        }
//    }
fun startAutoRefreshToken() {
    viewModelScope.launch {
        try {
            val refreshToken = tokenPreferences.getRefreshToken()
            if (!refreshToken.isNullOrEmpty()) {
                val response = repository.refreshToken(refreshToken)
                response.onSuccess { result ->
                    tokenPreferences.saveTokens(
                        accessToken = result.accessToken,
                        refreshToken = refreshToken
                    )
                    Log.d("TokenRefresh", "Access token refreshed!")
                }
                response.onFailure {
                    Log.e("TokenRefresh", "Failed to refresh token: ${it.message}")
                }
            } else {
                logout()
            }
        } catch (e: Exception) {
            Log.e("TokenRefresh", "Exception during token refresh", e)
            logout() // ✅ Logout nếu có lỗi bất ngờ
        }
    }
}


    fun getTokenData() = tokenPreferences.tokensFlow
}