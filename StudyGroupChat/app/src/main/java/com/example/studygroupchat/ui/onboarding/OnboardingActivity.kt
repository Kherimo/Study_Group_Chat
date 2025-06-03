package com.example.studygroupchat.ui.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.studygroupchat.adapter.OnboardingViewPagerAdapter
import com.example.studygroupchat.databinding.ActivityOnboardingBinding
import com.example.studygroupchat.ui.HomeActivity
import com.example.studygroupchat.ui.SplashActivity
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var viewPagerAdapter: OnboardingViewPagerAdapter

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

        // SỬA LỖI: Chuyển về SplashActivity để nó quyết định đi đâu tiếp
        val intent = Intent(this, SplashActivity::class.java)
        startActivity(intent)
        finish() // Đóng OnboardingActivity
    }
}