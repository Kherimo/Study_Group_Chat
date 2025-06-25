package com.example.studygroupchat.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studygroupchat.BuildConfig
import com.example.studygroupchat.MainActivity
import com.example.studygroupchat.R
import com.example.studygroupchat.adapters.AiChatAdapter
import com.example.studygroupchat.api.GeminiApiConfig
import com.example.studygroupchat.model.ai.ChatMessage // <-- Đã cập nhật đường dẫn import
import com.example.studygroupchat.repository.AiChatRepository
import com.example.studygroupchat.viewmodel.AiChatViewModel
import com.example.studygroupchat.viewmodel.AiChatViewModelFactory

class AIChatFragment : Fragment() {

    private lateinit var btnCloseChatAI: ImageButton
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var rvMessages: RecyclerView
    private lateinit var chatAdapter: AiChatAdapter

    private val viewModel: AiChatViewModel by activityViewModels {
        AiChatViewModelFactory(
            AiChatRepository(
                GeminiApiConfig.apiService,
                BuildConfig.GEMINI_API_KEY
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_a_i_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etMessage = view.findViewById(R.id.etMessage)
        btnSend = view.findViewById(R.id.btnSend)
        rvMessages = view.findViewById(R.id.rvMessages) // <-- Cập nhật ID thành rvMessages
        btnCloseChatAI = view.findViewById(R.id.btnCloseChatAI)

        setupRecyclerView()
        setupListeners()
        observeViewModel()

        // Focus input and show keyboard when entering the chat
        etMessage.requestFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(etMessage, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setupRecyclerView() {
        chatAdapter = AiChatAdapter(mutableListOf())
        rvMessages.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true // Tin nhắn mới sẽ xuất hiện ở dưới cùng
            }
        }
    }

    private fun setupListeners() {
        btnCloseChatAI.setOnClickListener {
            (activity as? MainActivity)?.showFabButton()
            parentFragmentManager.beginTransaction().remove(this@AIChatFragment).commit()
        }

        btnSend.setOnClickListener {
            val question = etMessage.text.toString().trim()
            if (question.isNotEmpty()) {
                viewModel.sendQuestion(question)
                etMessage.text.clear()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            // FIX: Khắc phục lỗi Type Mismatch bằng cách ánh xạ List<Message> sang List<ChatMessage>
            // Điều này giả định rằng lớp `Message` của ViewModel có các thuộc tính 'content' và 'sender'.
            val chatMessageList = messages.map { message ->
                ChatMessage(content = message.content, sender = message.sender)
            }
            chatAdapter.updateMessages(chatMessageList)
            rvMessages.scrollToPosition(chatAdapter.itemCount - 1) // Cuộn đến tin nhắn mới nhất
        }
    }
}
