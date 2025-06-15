package com.example.studygroupchat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.RecyclerView
import com.example.studygroupchat.R
import com.example.studygroupchat.model.room.RoomMessage

class MessageAdapter(
    private val messageList: List<RoomMessage>,
    var currentUserId: Int = 0
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SELF = 1
        private const val VIEW_TYPE_OTHER = 2
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        val tvSenderName: TextView = itemView.findViewById(R.id.tvSenderName)
        val imgAvatar: ImageView = itemView.findViewById(R.id.imgAvatar)
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return if (message.senderId == currentUserId) VIEW_TYPE_SELF else VIEW_TYPE_OTHER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutId = if (viewType == VIEW_TYPE_SELF) {
            R.layout.item_message_self
        } else {
            R.layout.item_message
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messageList[position]
        holder.tvMessage.text = message.content ?: ""
        val sender = message.sender
        holder.tvSenderName.text = sender?.fullName ?: sender?.userName ?: ""
        sender?.avatarUrl?.let {
            Glide.with(holder.itemView.context)
                .load(it)
                .placeholder(R.drawable.baseline_account_circle_24)
                .into(holder.imgAvatar)
        } ?: holder.imgAvatar.setImageResource(R.drawable.baseline_account_circle_24)
    }

    override fun getItemCount() = messageList.size
}