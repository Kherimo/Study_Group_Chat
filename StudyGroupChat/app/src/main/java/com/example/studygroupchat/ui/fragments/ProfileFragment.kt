package com.example.studygroupchat.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.studygroupchat.R
import com.example.studygroupchat.api.ApiConfig
import com.example.studygroupchat.repository.UserRepository
import com.example.studygroupchat.viewmodel.UserViewModel
import com.example.studygroupchat.viewmodel.UserViewModelFactory
import com.example.studygroupchat.viewmodel.AuthViewModel

/**
 * Hiển thị thông tin người dùng hiện tại.
 */
class ProfileFragment : Fragment() {
    private val viewModel: UserViewModel by viewModels {
        UserViewModelFactory(UserRepository(ApiConfig.userApiService))
    }
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvUsername = view.findViewById<TextView>(R.id.tvUsername)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmail)
        val tvPhone = view.findViewById<TextView>(R.id.tvPhone)
        val imgAvatar = view.findViewById<ImageView>(R.id.imgAvatar)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)
        val btnEdit = view.findViewById<TextView>(R.id.btnEditProfile)

        viewModel.user.observe(viewLifecycleOwner) { user ->
            tvUsername.text = user.fullName ?: user.userName
            tvEmail.text = user.email
            tvPhone.text = user.phoneNumber
        }

        viewModel.fetchCurrentUser()
        btnEdit.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, EditProfileFragment())
                .addToBackStack(null)
                .commit()
        }
        btnLogout.setOnClickListener {
            authViewModel.logout()
        }

    }
}
