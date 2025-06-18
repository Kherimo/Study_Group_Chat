package com.example.studygroupchat.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity

import com.example.studygroupchat.R
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
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
    private lateinit var btnSend: TextView

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(UserRepository(ApiConfig.userApiService))
    }

    private var currentUserId: Int = 0

    private val viewModel: RoomMessageViewModel by viewModels {
        RoomMessageViewModelFactory(RoomMessageRepository(ApiConfig.roomMessageApiService))
    }

    private var groupId: String? = null
    private var groupName: String? = null
    private var groupMode: String? = null
    private var memberCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarGroupChat)
        setSupportActionBar(toolbar)


// Lấy dữ liệu từ Intent
        groupId = intent.getStringExtra("groupId")
        groupName = intent.getStringExtra("groupName")
        groupMode = intent.getStringExtra("groupMode")
        memberCount = intent.getIntExtra("memberCount", 0)

        supportActionBar?.title = groupName ?: "Tên nhóm"
        val modeText = when (groupMode) {
            "public" -> "Công khai"
            "private" -> "Riêng tư"
            else -> groupMode ?: ""
        }
        supportActionBar?.subtitle = "$modeText • ${memberCount} thành viên"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        recyclerMessages = findViewById(R.id.recyclerMessages)
        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)

        messageList = mutableListOf()

        messageAdapter = MessageAdapter(messageList)
        recyclerMessages.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        recyclerMessages.adapter = messageAdapter

        etMessage.setOnClickListener {
            scrollToBottom()
        }
        etMessage.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                scrollToBottom()
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
            scrollToBottom()
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

    private fun scrollToBottom() {
        recyclerMessages.scrollToPosition(messageList.size - 1)
    }

    // Xử lý nút back trên thanh toolbar
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        // Lấy menu item
        val memberItem = menu.findItem(R.id.menu_members)

        // Gọi trực tiếp luôn hành động (không chờ click)
        memberItem.setOnMenuItemClickListener {
            startActivity(Intent(this, MemberListActivity::class.java))
            true
        }

        return true
    }

}