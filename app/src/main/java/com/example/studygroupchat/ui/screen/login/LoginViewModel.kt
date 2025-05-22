package com.example.studygroupchat.ui.screen.login

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.studygroupchat.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun saveToken(context: Context, token: String) {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().putString("token", token).apply()
    }

    fun login(navController: NavController, context: android.content.Context) {
        viewModelScope.launch {
            try {
                val response = repository.login(uiState.value.email, uiState.value.password)
                saveToken(context, response.token)
                Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Login failed") }
            }
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val error: String = ""
)
