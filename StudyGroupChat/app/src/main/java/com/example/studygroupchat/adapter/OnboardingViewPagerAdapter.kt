package com.example.studygroupchat.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.studygroupchat.ui.onboarding.OnboardingPageOneFragment
import com.example.studygroupchat.ui.onboarding.OnboardingPageThreeFragment
import com.example.studygroupchat.ui.onboarding.OnboardingPageTwoFragment

class OnboardingViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnboardingPageOneFragment()
            1 -> OnboardingPageTwoFragment()
            else -> OnboardingPageThreeFragment()
        }
    }
}