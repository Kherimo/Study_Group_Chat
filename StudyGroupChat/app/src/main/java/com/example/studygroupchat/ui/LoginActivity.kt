package com.example.studygroupchat.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.studygroupchat.BaseActivity
import com.example.studygroupchat.MainActivity
import com.example.studygroupchat.StudyGroupChatApplication
import com.example.studygroupchat.databinding.ActivityLoginBinding
import com.example.studygroupchat.viewmodel.AuthViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Sử dụng binding từ file XML mới
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        // Cập nhật logic cho nút Login để lấy text từ ID mới 'etEmailPhone'
        binding.btnLogin.setOnClickListener {
            val emailOrPhone = binding.etUsername.text.toString() // CẬP NHẬT ID
            val password = binding.etPassword.text.toString()

            if (emailOrPhone.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ViewModel login function có thể cần cập nhật để chấp nhận email hoặc phone
            viewModel.login(emailOrPhone, password)
        }

        // BỎ logic cũ cho TextView
        // binding.tvRegister.setOnClickListener { ... }

            binding.textRegister.setOnClickListener {
                // Chuyển sang RegisterActivity
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }

        }

            private fun observeViewModel() {
                viewModel.isLoading.observe(this) { isLoading ->
                    binding.btnLogin.isEnabled = !isLoading
                    binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
                }

                viewModel.loginResult.observe(this) { result ->
                    result.onSuccess { response ->
                        Log.d("LoginActivity", "Login successful, navigating to HomeActivity")
                        Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }

                        val app = application as StudyGroupChatApplication
                        lifecycleScope.launch {
                            binding.progressBar.visibility = android.view.View.VISIBLE
                            app.syncManager.syncAll()
                            binding.progressBar.visibility = android.view.View.GONE
                            startActivity(intent)
                            finish()
                        }
                    }.onFailure { error ->
                        Log.e("LoginActivity", "Login failed: ${error.message}")
                        Toast.makeText(this, "Username hoặc Mật khẩu không đúng", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }