package com.example.studygroupchat.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.studygroupchat.databinding.ActivityLoginBinding
import com.example.studygroupchat.viewmodel.AuthViewModel
import com.google.android.material.tabs.TabLayout

class LoginActivity : AppCompatActivity() {
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
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ViewModel login function có thể cần cập nhật để chấp nhận email hoặc phone
            viewModel.login(emailOrPhone, password)
        }

        // BỎ logic cũ cho TextView
        // binding.tvRegister.setOnClickListener { ... }

        // --- BỔ SUNG: LOGIC MỚI CHO TABLAYOUT ---
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Khi tab "Register" (vị trí 1) được chọn
                if (tab?.position == 1) {
                    // Chuyển sang RegisterActivity
                    val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(intent)
                    // Reset lại tab về "Login" sau khi quay lại
                    binding.tabLayout.getTabAt(0)?.select()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        // --- KẾT THÚC BỔ SUNG ---
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.btnLogin.isEnabled = !isLoading
            binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
        }

        viewModel.loginResult.observe(this) { result ->
            result.onSuccess { response ->
                Log.d("LoginActivity", "Login successful, navigating to HomeActivity")
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            }.onFailure { error ->
                Log.e("LoginActivity", "Login failed: ${error.message}")
                Toast.makeText(this, error.message ?: "Login failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}