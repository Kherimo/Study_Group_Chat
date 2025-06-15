package com.example.studygroupchat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvDateHeader: TextView = itemView.findViewById(R.id.tvDateHeader)
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
                .circleCrop()
                .into(holder.imgAvatar)
        } ?: holder.imgAvatar.setImageResource(R.drawable.baseline_account_circle_24)

        val prev = if (position > 0) messageList[position - 1] else null
        val next = if (position < messageList.size - 1) messageList[position + 1] else null
        val groupWithPrev = prev != null && sameSender(prev, message) && closeTime(prev, message)
        val groupWithNext = next != null && sameSender(next, message) && closeTime(next, message)

        when {
            groupWithPrev && groupWithNext -> holder.tvTime.visibility = View.GONE
            !groupWithPrev && groupWithNext -> holder.tvTime.visibility = View.GONE
            else -> {
                holder.tvTime.visibility = View.VISIBLE
                holder.tvTime.text = formatTime(message.sentAt)
            }
        }

        if (position == 0 || prev == null || !sameDay(message.sentAt, prev.sentAt)) {
            holder.tvDateHeader.visibility = View.VISIBLE
            holder.tvDateHeader.text = formatDate(message.sentAt)
        } else {
            holder.tvDateHeader.visibility = View.GONE
        }

        val isSelf = getItemViewType(position) == VIEW_TYPE_SELF
        if (isSelf) {
            holder.tvSenderName.visibility = View.GONE
            holder.imgAvatar.visibility = View.GONE
        } else {
            when {
                groupWithPrev && groupWithNext -> {
                    holder.tvSenderName.visibility = View.GONE
                    holder.imgAvatar.visibility = View.INVISIBLE
                }
                groupWithPrev && !groupWithNext -> {
                    holder.tvSenderName.visibility = View.GONE
                    holder.imgAvatar.visibility = View.INVISIBLE
                }
                !groupWithPrev && groupWithNext -> {
                    holder.tvSenderName.visibility = View.VISIBLE
                    holder.imgAvatar.visibility = View.VISIBLE
                }
                else -> {
                    holder.tvSenderName.visibility = View.VISIBLE
                    holder.imgAvatar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun formatTime(raw: String): String {
        val dt = LocalDateTime.parse(raw, DateTimeFormatter.ISO_DATE_TIME)
            .atZone(ZoneId.systemDefault())
        return dt.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    private fun formatDate(raw: String): String {
        val dt = LocalDateTime.parse(raw, DateTimeFormatter.ISO_DATE_TIME)
            .atZone(ZoneId.systemDefault())
        return dt.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }

    private fun sameDay(a: String, b: String): Boolean {
        val d1 = LocalDate.parse(a.substring(0, 10))
        val d2 = LocalDate.parse(b.substring(0, 10))
        return d1 == d2
    }

    private fun sameSender(a: RoomMessage, b: RoomMessage): Boolean {
        return a.senderId == b.senderId
    }

    private fun parseInstant(raw: String): Long {
        return LocalDateTime.parse(raw, DateTimeFormatter.ISO_DATE_TIME)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    private fun closeTime(a: RoomMessage, b: RoomMessage): Boolean {
        val diff = kotlin.math.abs(parseInstant(a.sentAt) - parseInstant(b.sentAt))
        return diff <= Duration.ofMinutes(3).toMillis()
    }

    override fun getItemCount() = messageList.size
}