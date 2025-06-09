package com.example.studygroupchat.ui.fragments

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.example.studygroupchat.R
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studygroupchat.adapter.MessageAdapter
import com.example.studygroupchat.model.Message

class GroupChatActivity : AppCompatActivity() {

    private lateinit var messageList: MutableList<Message>
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var recyclerMessages: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageButton

    private var groupId: String? = null
    private var groupName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)

        groupId = intent.getStringExtra("groupId")
        groupName = intent.getStringExtra("groupName")

        supportActionBar?.title = groupName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerMessages = findViewById(R.id.recyclerMessages)
        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)

        messageList = mutableListOf(
            Message("A", "Xin chào mọi người"),
            Message("B", "Chào bạn"),
        )

        messageAdapter = MessageAdapter(messageList)
        recyclerMessages.layoutManager = LinearLayoutManager(this)
        recyclerMessages.adapter = messageAdapter

        btnSend.setOnClickListener {
            val content = etMessage.text.toString().trim()
            if (content.isNotEmpty()) {
                val message = Message("You", content)
                messageList.add(message)
                messageAdapter.notifyItemInserted(messageList.size - 1)
                recyclerMessages.scrollToPosition(messageList.size - 1)
                etMessage.text.clear()

                // TODO: gửi message lên server nếu cần
            }
        }
    }

    // Xử lý nút back trên thanh toolbar
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
