package com.example.studygroupchat.ui.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.studygroupchat.MainActivity
import com.example.studygroupchat.adapter.OnboardingViewPagerAdapter
import com.example.studygroupchat.databinding.ActivityOnboardingBinding
import com.example.studygroupchat.ui.LoginActivity
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

        // Khối logic được cập nhật để quản lý tất cả các nút
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                val isLastPage = position == viewPagerAdapter.itemCount - 1
                val isFirstPage = position == 0

                // Cập nhật nút "Tiếp theo" và "Bỏ qua"
                binding.btnNext.visibility = if (isLastPage) View.VISIBLE else View.GONE
                binding.btnSkip.visibility = if (isLastPage) View.GONE else View.VISIBLE

                // Cập nhật nút điều hướng trái (ẩn ở trang đầu)
                binding.btnPrev.visibility = if (isFirstPage) View.GONE else View.VISIBLE

                // Cập nhật nút điều hướng phải (ẩn ở trang cuối)
                binding.btnRight.visibility = if (isLastPage) View.GONE else View.VISIBLE
            }
        })

        binding.btnNext.setOnClickListener {
            onOnboardingFinished()
        }

        binding.btnSkip.setOnClickListener {
            onOnboardingFinished()
        }

        binding.btnRight.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem < viewPagerAdapter.itemCount - 1) {
                binding.viewPager.currentItem = currentItem + 1
            }
        }

        binding.btnPrev.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem > 0) {
                binding.viewPager.currentItem = currentItem - 1
            }
        }
        // Ẩn nút "trái" vì màn hình bắt đầu ở trang đầu tiên.
        binding.btnPrev.visibility = View.GONE
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