package com.example.studygroupchat.ui.fragments

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.example.studygroupchat.R
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.activity.viewModels
import com.example.studygroupchat.adapter.MessageAdapter
import com.example.studygroupchat.api.ApiConfig
import com.example.studygroupchat.model.room.RoomMessage
import com.example.studygroupchat.repository.RoomMessageRepository
import com.example.studygroupchat.repository.UserRepository
import com.example.studygroupchat.viewmodel.RoomMessageViewModel
import com.example.studygroupchat.viewmodel.RoomMessageViewModelFactory
import com.example.studygroupchat.viewmodel.UserViewModel
import com.example.studygroupchat.viewmodel.UserViewModelFactory
import com.google.android.material.appbar.MaterialToolbar

class GroupChatActivity : AppCompatActivity() {

    private lateinit var messageList: MutableList<RoomMessage>
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var recyclerMessages: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageButton

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(UserRepository(ApiConfig.userApiService))
    }

    private var currentUserId: Int = 0

    private val viewModel: RoomMessageViewModel by viewModels {
        RoomMessageViewModelFactory(RoomMessageRepository(ApiConfig.roomMessageApiService))
    }

    private var groupId: String? = null
    private var groupName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarGroupChat)
        setSupportActionBar(toolbar)

// Lấy dữ liệu từ Intent
        groupId = intent.getStringExtra("groupId")
        groupName = intent.getStringExtra("groupName")

        supportActionBar?.title = groupName ?: "Tên nhóm"
        supportActionBar?.subtitle = "Lớp học Toán 10A"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        recyclerMessages = findViewById(R.id.recyclerMessages)
        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)

        messageList = mutableListOf()

        messageAdapter = MessageAdapter(messageList)
        recyclerMessages.layoutManager = LinearLayoutManager(this)
        recyclerMessages.adapter = messageAdapter

        etMessage.setOnClickListener {
            recyclerMessages.scrollToPosition(messageList.size - 1)
        }
        etMessage.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                recyclerMessages.scrollToPosition(messageList.size - 1)
            }
        }

        userViewModel.user.observe(this) { user ->
            currentUserId = user.userId
            messageAdapter.currentUserId = currentUserId
            messageAdapter.notifyDataSetChanged()
        }

        userViewModel.fetchCurrentUser()

        viewModel.messages.observe(this) { messages ->
            messageList.clear()
            messageList.addAll(messages)
            messageAdapter.notifyDataSetChanged()
            recyclerMessages.scrollToPosition(messageList.size - 1)
        }

        groupId?.let { viewModel.fetchRoomMessages(it) }

        btnSend.setOnClickListener {
            val content = etMessage.text.toString().trim()
            if (content.isNotEmpty()) {
                groupId?.let { viewModel.sendRoomMessage(it, content) }
                etMessage.text.clear()
            }
        }
    }

    // Xử lý nút back trên thanh toolbar
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}