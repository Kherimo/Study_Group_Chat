package com.example.studygroupchat.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.studygroupchat.databinding.FragmentOnboardingPageBinding

class OnboardingPageThreeFragment : Fragment() {
    private var _binding: FragmentOnboardingPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOnboardingPageBinding.inflate(inflater, container, false)

        binding.lottieAnimationView.setAnimation("onboarding3.json")
        binding.tvTitle.text = "Bắt đầu hành trình"
        binding.tvDescription.text = "Hãy đăng nhập hoặc đăng ký để bắt đầu hành trình chinh phục tri thức của bạn ngay hôm nay!"

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}