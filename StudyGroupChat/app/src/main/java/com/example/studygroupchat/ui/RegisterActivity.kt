package com.example.studygroupchat.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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

        // --- BỔ SUNG: LOGIC MỚI CHO TABLAYOUT ---
        // Mặc định chọn tab "Register" (vị trí 1)
        binding.tabLayout.getTabAt(1)?.select()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Khi tab "Login" (vị trí 0) được chọn
                if (tab?.position == 0) {
                    // Đóng màn hình hiện tại để quay về LoginActivity
                    finish()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        // --- KẾT THÚC BỔ SUNG ---
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.btnRegister.isEnabled = !isLoading
            binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
        }

        viewModel.registerResult.observe(this) { result ->
            result.onSuccess { response ->
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java).apply {
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