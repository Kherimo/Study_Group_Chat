package com.example.studygroupchat.ui.fragments

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studygroupchat.R
import com.example.studygroupchat.StudyGroupChatApplication
import com.example.studygroupchat.adapter.MemberAdapter
import com.example.studygroupchat.api.ApiConfig
import com.example.studygroupchat.repository.RoomRepository
import com.example.studygroupchat.repository.UserRepository
import com.example.studygroupchat.viewmodel.RoomViewModel
import com.example.studygroupchat.viewmodel.RoomViewModelFactory
import com.example.studygroupchat.viewmodel.UserViewModel
import com.example.studygroupchat.viewmodel.UserViewModelFactory
import com.example.studygroupchat.model.room.RoomMember
import com.google.android.material.appbar.MaterialToolbar
import androidx.activity.viewModels

class MemberListActivity : AppCompatActivity() {

    private lateinit var recyclerMembers: RecyclerView
    private var memberAdapter: MemberAdapter? = null
    private var members: List<RoomMember> = emptyList()
    private var currentUserId: Int = 0
    private var ownerId: Int = 0

    private val userViewModel: UserViewModel by viewModels {
        val app = application as StudyGroupChatApplication
        UserViewModelFactory(
            UserRepository(
                ApiConfig.userApiService,
                app.database.userDao()
            )
        )
    }

    private val viewModel: RoomViewModel by viewModels {
        val app = application as StudyGroupChatApplication
        RoomViewModelFactory(
            RoomRepository(
                ApiConfig.roomApiService,
                app.database.roomDao()
            )
        )
    }

    private var roomId: String? = null
    private var roomName: String? = null
    private var initialCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_member_list)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        roomId = intent.getStringExtra("groupId")
        roomName = intent.getStringExtra("groupName")
        initialCount = intent.getIntExtra("memberCount", 0)

        supportActionBar?.apply {
            title = "Thành viên"
            subtitle = "${roomName ?: ""} • ${initialCount} thành viên"
            setDisplayHomeAsUpEnabled(true)
        }

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        recyclerMembers = findViewById(R.id.recyclerMembers)
        recyclerMembers.layoutManager = LinearLayoutManager(this)

        userViewModel.user.observe(this) { user ->
            currentUserId = user.userId
            refreshAdapter()
        }
        userViewModel.fetchCurrentUser()

        viewModel.members.observe(this) { list ->
            members = list
            refreshAdapter()
            supportActionBar?.subtitle = "${roomName ?: ""} • ${list.size} thành viên"
        }

        viewModel.selectedRoom.observe(this) { result ->
            result.onSuccess { room ->
                ownerId = room.ownerId
                refreshAdapter()
            }
        }

        roomId?.let {
            viewModel.getRoom(it)
            viewModel.fetchRoomMembers(it)
        }
    }

    private fun refreshAdapter() {
        if (memberAdapter == null) {
            memberAdapter = MemberAdapter(members, currentUserId, ownerId, onMoreClick = null, showMenu = false)
            recyclerMembers.adapter = memberAdapter
        } else {
            memberAdapter?.updateData(members)
        }
    }
}