package com.example.studygroupchat.ui.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.studygroupchat.MainActivity
import com.example.studygroupchat.adapter.OnboardingViewPagerAdapter
import com.example.studygroupchat.databinding.ActivityOnboardingBinding
import com.example.studygroupchat.ui.HomeActivity
import com.example.studygroupchat.ui.LoginActivity
import com.example.studygroupchat.ui.SplashActivity
import com.example.studygroupchat.viewmodel.AuthViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var viewPagerAdapter: OnboardingViewPagerAdapter
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPagerAdapter = OnboardingViewPagerAdapter(this)
        binding.viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()

        binding.btnNext.setOnClickListener {
            if (binding.viewPager.currentItem < viewPagerAdapter.itemCount - 1) {
                binding.viewPager.currentItem += 1
            } else {
                onOnboardingFinished()
            }
        }

        binding.btnSkip.setOnClickListener {
            onOnboardingFinished()
        }
    }

    private fun onOnboardingFinished() {
        // Lưu lại là đã xem onboarding
        val sharedPref = getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("isFinished", true)
            apply()
        }

        lifecycleScope.launch {
            val userData = viewModel.getTokenData().first()
            val targetActivity = if (userData.accessToken.isNotEmpty()) {
                MainActivity::class.java
            } else {
                LoginActivity::class.java
            }

            startActivity(Intent(this@OnboardingActivity, targetActivity).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }
    }
}