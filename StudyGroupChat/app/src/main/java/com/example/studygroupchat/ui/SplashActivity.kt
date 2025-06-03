package com.example.studygroupchat.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.studygroupchat.R
import com.example.studygroupchat.viewmodel.AuthViewModel
import com.example.studygroupchat.ui.onboarding.OnboardingActivity // Đảm bảo import đúng
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val SPLASH_DELAY: Long = 1500
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            // BẮT ĐẦU PHẦN LOGIC MỚI

            // 1. Kiểm tra trạng thái Onboarding trước tiên
            val sharedPref = getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
            val isOnboardingFinished = sharedPref.getBoolean("isFinished", false)

            if (!isOnboardingFinished) {
                // Nếu chưa xem Onboarding -> Chuyển đến OnboardingActivity
                startActivity(Intent(this, OnboardingActivity::class.java))
                finish() // Kết thúc SplashActivity
            } else {
                // Nếu đã xem Onboarding -> Tiếp tục logic kiểm tra đăng nhập như cũ của bạn
                checkUserLoginStatus()
            }
            // KẾT THÚC PHẦN LOGIC MỚI

        }, SPLASH_DELAY)
    }

    private fun checkUserLoginStatus() {
        lifecycleScope.launch {
            val userData = viewModel.getUserData().first()
            val targetActivity = if (userData.userId.isNotEmpty()) {
                // Đã đăng nhập -> Vào Home
                HomeActivity::class.java
            } else {
                // Chưa đăng nhập -> Vào Login
                LoginActivity::class.java
            }

            startActivity(Intent(this@SplashActivity, targetActivity).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish() // Kết thúc SplashActivity
        }
    }
}