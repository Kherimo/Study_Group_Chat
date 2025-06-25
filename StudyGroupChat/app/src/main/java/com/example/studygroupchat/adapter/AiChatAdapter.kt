package com.example.studygroupchat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studygroupchat.R
import com.example.studygroupchat.model.ai.ChatMessage

class AiChatAdapter(
    private val messages: MutableList<ChatMessage>
) : RecyclerView.Adapter<AiChatAdapter.MessageViewHolder>() {

    private val VIEW_TYPE_USER = 1
    private val VIEW_TYPE_AI = 2

    // Base ViewHolder
    open class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        open fun bind(message: ChatMessage) {}
    }

    // ViewHolder cho tin nhắn của người dùng
    class UserMessageViewHolder(itemView: View) : MessageViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.tvMessage)
        override fun bind(message: ChatMessage) {
            messageText.text = message.content
        }
    }

    // ViewHolder cho tin nhắn của AI
    class AiMessageViewHolder(itemView: View) : MessageViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.tvMessage)
        override fun bind(message: ChatMessage) {
            messageText.text = message.content
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].sender == "user") {
            VIEW_TYPE_USER
        } else {
            VIEW_TYPE_AI
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return if (viewType == VIEW_TYPE_USER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_user, parent, false)
            UserMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_ai, parent, false)
            AiMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun updateMessages(newMessages: List<ChatMessage>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged() // Để đơn giản. Để có hiệu năng tốt hơn, hãy dùng DiffUtil.
    }
}
