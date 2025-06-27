package com.example.studygroupchat.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.imageview.ShapeableImageView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.studygroupchat.R
import com.bumptech.glide.Glide
import com.example.studygroupchat.api.ApiConfig
import com.example.studygroupchat.repository.UserRepository
import com.example.studygroupchat.repository.RoomRepository
import com.example.studygroupchat.repository.RoomMessageRepository
import com.example.studygroupchat.StudyGroupChatApplication
import com.example.studygroupchat.viewmodel.UserViewModel
import com.example.studygroupchat.viewmodel.UserViewModelFactory
import com.example.studygroupchat.viewmodel.AuthViewModel
import com.example.studygroupchat.viewmodel.StatsViewModel
import com.example.studygroupchat.viewmodel.StatsViewModelFactory

/**
 * Hiển thị thông tin người dùng hiện tại.
 */
class ProfileFragment : Fragment() {
    private val viewModel: UserViewModel by viewModels {
        val app = requireActivity().application as StudyGroupChatApplication
        UserViewModelFactory(
            UserRepository(
                ApiConfig.userApiService,
                app.database.userDao()
            )
        )
    }
    private val authViewModel: AuthViewModel by activityViewModels()
    private val statsViewModel: StatsViewModel by viewModels {
        val app = requireActivity().application as StudyGroupChatApplication
        StatsViewModelFactory(
            UserRepository(ApiConfig.userApiService, app.database.userDao()),
            RoomRepository(ApiConfig.roomApiService, app.database.roomDao()),
            RoomMessageRepository(ApiConfig.roomMessageApiService, app.database.messageDao())
        )
    }

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
        val imgAvatar = view.findViewById<ShapeableImageView>(R.id.imgAvatar)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)
        val btnEdit = view.findViewById<TextView>(R.id.btnEditProfile)
        val tvRoomCount = view.findViewById<TextView>(R.id.tvCountClass)
        val tvMessageCount = view.findViewById<TextView>(R.id.tvCountMessage)

        viewModel.user.observe(viewLifecycleOwner) { user ->
            tvUsername.text = user.fullName ?: user.userName
            tvEmail.text = user.email
            tvPhone.text = user.phoneNumber
            user.avatarUrl?.let {
                Glide.with(this)
                    .load(it)
                    .placeholder(R.drawable.baseline_account_circle_24)
                    .into(imgAvatar)
            }
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

        statsViewModel.roomCount.observe(viewLifecycleOwner) { count ->
            tvRoomCount.text = count.toString()
        }
        statsViewModel.messageCount.observe(viewLifecycleOwner) { count ->
            tvMessageCount.text = count.toString()
        }

        statsViewModel.loadStats()

    }
}