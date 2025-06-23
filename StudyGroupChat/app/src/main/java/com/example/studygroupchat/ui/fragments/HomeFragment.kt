package com.example.studygroupchat.ui.fragments

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.ProgressBar
import com.example.studygroupchat.R
import com.example.studygroupchat.adapter.RoomAdapter
import com.example.studygroupchat.api.ApiConfig
import com.example.studygroupchat.repository.RoomRepository
import com.example.studygroupchat.repository.UserRepository
import com.example.studygroupchat.StudyGroupChatApplication
import com.example.studygroupchat.viewmodel.RoomViewModel
import com.example.studygroupchat.viewmodel.RoomViewModelFactory
import com.example.studygroupchat.viewmodel.UserViewModel
import com.example.studygroupchat.viewmodel.UserViewModelFactory

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RoomAdapter
    private lateinit var progressBar: ProgressBar
    private var currentUserId: Int = 0
    private val viewModel: RoomViewModel by viewModels {
        val app = requireActivity().application as StudyGroupChatApplication
        RoomViewModelFactory(
            RoomRepository(
                ApiConfig.roomApiService,
                app.database.roomDao()
            )
        )
    }

    private val userViewModel: UserViewModel by viewModels {
        val app = requireActivity().application as StudyGroupChatApplication
        UserViewModelFactory(
            UserRepository(
                ApiConfig.userApiService,
                app.database.userDao()
            )
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        adapter = RoomAdapter(emptyList(), isJoinRoom = false, onJoinClick = null) { room ->
            val fragment = GroupDetailFragment()
            fragment.arguments = Bundle().apply {
                putSerializable("room", room)
                putString("roomId", room.roomId.toString())
                putBoolean("isOwner", room.ownerId == currentUserId)
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        userViewModel.user.observe(viewLifecycleOwner) { user ->
            currentUserId = user.userId
        }
        userViewModel.fetchCurrentUser()

        viewModel.rooms.observe(viewLifecycleOwner) { rooms ->
            adapter.updateData(rooms)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.fetchMyRooms()

        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
            ) {
                outRect.top = 10 // khoảng cách giữa các item
                outRect.bottom = 10
            }
        })
    }

}