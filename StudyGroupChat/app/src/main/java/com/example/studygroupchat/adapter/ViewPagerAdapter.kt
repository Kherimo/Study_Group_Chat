package com.example.studygroupchat.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.studygroupchat.ui.fragments.ChangePasswordFragment
import com.example.studygroupchat.ui.fragments.EditInfoFragment

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2 // Có 3 tab

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> EditInfoFragment()
            1 -> ChangePasswordFragment()
            else -> Fragment()
        }
    }
}
