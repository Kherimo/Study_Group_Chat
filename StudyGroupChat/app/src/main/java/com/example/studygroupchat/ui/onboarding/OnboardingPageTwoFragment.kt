package com.example.studygroupchat.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.studygroupchat.databinding.FragmentOnboardingPageBinding

class OnboardingPageTwoFragment : Fragment() {
    private var _binding: FragmentOnboardingPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOnboardingPageBinding.inflate(inflater, container, false)

        binding.lottieAnimationView.setAnimation("onboarding2.json")
        binding.tvTitle.text = "Trò chuyện và Thảo luận"
        binding.tvDescription.text = "Tham gia vào các cuộc thảo luận sôi nổi, đặt câu hỏi và giải đáp thắc mắc cùng bạn bè."

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}