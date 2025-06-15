package com.example.studygroupchat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.studygroupchat.R
import com.example.studygroupchat.model.room.Room
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class RoomAdapter(
    private var roomList: List<Room>,
    private val isJoinRoom: Boolean,
    private val onJoinClick: ((Room) -> Unit)? = null
) :  // true nếu ở JoinRoomFragment
    RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    inner class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseTitle: TextView = itemView.findViewById(R.id.course_title)
        val courseDescription: TextView = itemView.findViewById(R.id.course_description)
        val courseStatus: TextView = itemView.findViewById(R.id.course_status)
        val courseMember: TextView = itemView.findViewById(R.id.course_member)
        val tvTimeCreate: TextView = itemView.findViewById(R.id.tvTimeCreate)
        val btnJoin: Button = itemView.findViewById(R.id.btnJoin)
        val tvMember: TextView = itemView.findViewById(R.id.tvmember)
        val tvTime: TextView = itemView.findViewById(R.id.tvtime)
    }

    private fun formatCreatedAt(raw: String?): String {
        if (raw.isNullOrBlank()) return ""
        return try {
            val dt = LocalDateTime.parse(raw, DateTimeFormatter.ISO_DATE_TIME)
            dt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        } catch (e: Exception) {
            raw ?: ""
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = roomList[position]
//        holder.courseTitle.text = room.roomName
        holder.courseTitle.text = room.roomName
        holder.courseDescription.text = room.description ?: ""
        holder.courseStatus.text = if (room.isActive) "Đang học" else "Đã dừng"

        // Xử lý trạng thái màu chữ và background
        val textColorRes = if (room.isActive) R.color.green_600 else R.color.orange_600
        val backgroundRes = if (room.isActive) R.drawable.bg_status_active else R.drawable.bg_status_pause

        // Áp dụng màu chữ cho textStatus
        holder.courseStatus.setTextColor(
            ContextCompat.getColor(holder.itemView.context, textColorRes)
        )

        // Áp dụng background cho courseStatus
        holder.courseStatus.setBackgroundResource(backgroundRes)

        // Hiển thị các view theo điều kiện
        if (isJoinRoom) {
            holder.courseMember.text = room.members?.size?.toString() ?: "0"
            holder.tvTimeCreate.text = "Tạo ngày: ${formatCreatedAt(room.createdAt)}"
            holder.courseMember.visibility = View.VISIBLE
            holder.courseStatus.visibility = View.GONE
            holder.tvTime.visibility = View.GONE
            holder.tvMember.visibility = View.GONE
            holder.tvTimeCreate.visibility = View.VISIBLE
            holder.btnJoin.visibility = View.VISIBLE
            holder.btnJoin.setOnClickListener { onJoinClick?.invoke(room) }

        } else {
            val memberCount = room.members?.size ?: 0
            val ownerName = room.owner?.fullName ?: room.owner?.userName ?: ""
            holder.tvMember.text = "$ownerName • $memberCount thành viên"
            holder.courseMember.visibility = View.GONE
            holder.tvTimeCreate.visibility = View.GONE
            holder.btnJoin.visibility = View.GONE
            holder.courseStatus.visibility = View.VISIBLE
            holder.tvTime.visibility = View.VISIBLE
            holder.tvMember.visibility = View.VISIBLE
        }
    }


    override fun getItemCount(): Int = roomList.size

    fun updateData(newList: List<Room>) {
        roomList = newList
        notifyDataSetChanged()
    }
}