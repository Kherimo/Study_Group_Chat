package com.example.studygroupchat.ui.screen.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    var fullNameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }
    var agreePolicy by remember { mutableStateOf(false) }
    var agreePolicyError by remember { mutableStateOf("") }
    val context = LocalContext.current

    fun validate(): Boolean {
        fullNameError = ""
        emailError = ""
        passwordError = ""
        confirmPasswordError = ""
        agreePolicyError = ""
        var valid = true

        if (state.fullName.isBlank()) {
            fullNameError = "Vui lòng nhập họ tên"
            valid = false
        }
        if (state.email.isBlank()) {
            emailError = "Vui lòng nhập email"
            valid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            emailError = "Email không hợp lệ"
            valid = false
        }
        if (state.password.isBlank()) {
            passwordError = "Vui lòng nhập mật khẩu"
            valid = false
        }
        if (confirmPassword.isBlank()) {
            confirmPasswordError = "Vui lòng nhập lại mật khẩu"
            valid = false
        } else if (confirmPassword != state.password) {
            confirmPasswordError = "Mật khẩu không khớp"
            valid = false
        }
        if (!agreePolicy) {
            agreePolicyError = "Bạn phải đồng ý với chính sách"
            valid = false
        }
        return valid
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Đăng ký", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = state.fullName,
            onValueChange = {
                viewModel.onFullNameChanged(it)
                if (fullNameError.isNotEmpty()) fullNameError = ""
            },
            label = { Text("Họ tên") },
            modifier = Modifier.fillMaxWidth(),
            isError = fullNameError.isNotEmpty()
        )
        if (fullNameError.isNotEmpty()) {
            Text(fullNameError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        OutlinedTextField(
            value = state.email,
            onValueChange = {
                viewModel.onEmailChanged(it)
                if (emailError.isNotEmpty()) emailError = ""
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = emailError.isNotEmpty(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        if (emailError.isNotEmpty()) {
            Text(emailError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        OutlinedTextField(
            value = state.password,
            onValueChange = {
                viewModel.onPasswordChanged(it)
                if (passwordError.isNotEmpty()) passwordError = ""
            },
            label = { Text("Mật khẩu") },
            modifier = Modifier.fillMaxWidth(),
            isError = passwordError.isNotEmpty(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        if (passwordError.isNotEmpty()) {
            Text(passwordError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                if (confirmPasswordError.isNotEmpty()) confirmPasswordError = ""
            },
            label = { Text("Nhập lại mật khẩu") },
            modifier = Modifier.fillMaxWidth(),
            isError = confirmPasswordError.isNotEmpty(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        if (confirmPasswordError.isNotEmpty()) {
            Text(confirmPasswordError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = agreePolicy,
                onCheckedChange = {
                    agreePolicy = it
                    if (agreePolicyError.isNotEmpty()) agreePolicyError = ""
                }
            )
            Text("Tôi đồng ý với chính sách...")
        }
        if (agreePolicyError.isNotEmpty()) {
            Text(agreePolicyError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (validate()) viewModel.register(navController, context)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Đăng ký")
        }

        TextButton(
            onClick = { navController.navigate("login") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Đã có tài khoản? Đăng nhập")
        }

        if (state.error.isNotEmpty()) {
            Text(
                text = state.error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}