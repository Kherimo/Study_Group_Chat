package com.example.studygroupchat.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.studygroupchat.MainActivity
import com.example.studygroupchat.databinding.ActivityRegisterBinding
import com.example.studygroupchat.viewmodel.AuthViewModel
import com.google.android.material.tabs.TabLayout

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Sử dụng binding từ file XML mới
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.btnRegister.setOnClickListener {
            // Logic đăng ký giữ nguyên, các ID trường nhập liệu đã chính xác
            val userName = binding.etUsername.text.toString()
            val email = binding.etEmail.text.toString()
            val phoneNumber = binding.etPhone.text.toString()
            val password = binding.etPassword.text.toString()
            val fullName = binding.etFullName.text.toString()

            if (userName.isBlank() || email.isBlank() || phoneNumber.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.register(
                userName = userName,
                email = email,
                phoneNumber = phoneNumber,
                password = password,
                fullName = fullName.takeIf { it.isNotBlank() }
            )
        }

        // BỎ logic cũ cho TextView
        // binding.tvLogin.setOnClickListener { finish() }

        binding.textLogin.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.btnRegister.isEnabled = !isLoading
            binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
        }

        viewModel.registerResult.observe(this) { result ->
            result.onSuccess { response ->
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            }.onFailure { error ->
                Toast.makeText(this, error.message ?: "Registration failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}