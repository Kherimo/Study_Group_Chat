package com.example.studygroupchat.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.example.studygroupchat.MainActivity
import com.example.studygroupchat.R

class AIChatFragment : Fragment() {

    private lateinit var btnCloseChatAI:ImageButton
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var layoutResponse: LinearLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_a_i_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        etMessage = view.findViewById(R.id.etMessage)
        btnSend = view.findViewById(R.id.btnSend)
        layoutResponse = view.findViewById(R.id.layoutResponse)
        btnCloseChatAI = view.findViewById(R.id.btnCloseChatAI)

        // Bắt sự kiện đóng
        btnCloseChatAI.setOnClickListener {
            // Hiển thị lại FAB trong Activity
            (activity as? MainActivity)?.showFabButton()

            // Đóng fragment
            parentFragmentManager.beginTransaction()
                .remove(this@AIChatFragment)
                .commit()
        }
        btnSend.setOnClickListener {
            val question = etMessage.text.toString()
            if (question.isNotEmpty()) {
                showUserQuestion(question)
                simulateAIResponse(question)
                etMessage.text.clear()
            }
        }
    }

    private fun showUserQuestion(text: String) {
        val tv = TextView(requireContext())
        tv.text = "Bạn: $text"
        layoutResponse.addView(tv)
    }

    private fun simulateAIResponse(text: String) {
        val tv = TextView(requireContext())
        tv.text = "AI: Đây là phản hồi mẫu cho \"$text\""
        layoutResponse.addView(tv)
    }
}
