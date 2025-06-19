package com.example.studygroupchat.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.lifecycle.lifecycleScope
import com.example.studygroupchat.MainActivity
import com.example.studygroupchat.R
import com.example.studygroupchat.viewmodel.AuthViewModel
import com.example.studygroupchat.ui.onboarding.OnboardingActivity // Đảm bảo import đúng
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()
    private lateinit var progressBar: android.widget.ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        progressBar = findViewById(R.id.progressBar)

        // Kiểm tra trạng thái Onboarding trước tiên
        val sharedPref = getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        val isOnboardingFinished = sharedPref.getBoolean("isFinished", false)

        if (!isOnboardingFinished) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
        } else {
            checkUserLoginStatus()
        }
    }

    private fun checkUserLoginStatus() {
        lifecycleScope.launch {
            val userData = viewModel.getTokenData().first()
            val targetActivity: Class<*> = if (userData.accessToken.isNotEmpty()) {
                if (isNetworkAvailable()) {
                    progressBar.visibility = android.view.View.VISIBLE
                    val app = application as com.example.studygroupchat.StudyGroupChatApplication
                    app.syncManager.syncAll()
                    progressBar.visibility = android.view.View.GONE
                }
                MainActivity::class.java
            } else {
                LoginActivity::class.java
            }

            startActivity(Intent(this@SplashActivity, targetActivity).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = getSystemService<ConnectivityManager>() ?: return false
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}