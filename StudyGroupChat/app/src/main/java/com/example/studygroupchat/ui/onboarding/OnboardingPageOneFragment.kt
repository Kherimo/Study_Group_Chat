package com.example.studygroupchat.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.studygroupchat.databinding.FragmentOnboardingPageBinding

class OnboardingPageOneFragment : Fragment() {
    private var _binding: FragmentOnboardingPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOnboardingPageBinding.inflate(inflater, container, false)

        binding.lottieAnimationView.setAnimation("onboarding1.json")
        binding.tvTitle.text = "Chào mừng đến EduConnect"
        binding.tvDescription.text = "Khám phá các nhóm học tập, chia sẻ kiến thức và cùng nhau tiến bộ mỗi ngày."

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}