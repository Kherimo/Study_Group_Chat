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
import com.example.studygroupchat.model.Room
import com.example.studygroupchat.model.RoomWithExtras



class RoomAdapter(
    private var roomList: List<Room>,
    private val isJoinRoom: Boolean ) :  // true nếu ở JoinRoomFragment
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

        fun bind(room: Room) {
            courseTitle.text = room.roomName
            courseDescription.text = room.description
            courseStatus.text = if (room.isActive) "Đang học" else "Tạm dừng"
            courseStatus.setBackgroundResource(if (room.isActive) R.drawable.bg_status_active else R.drawable.bg_status_pause)
            courseStatus.setTextColor(
                ContextCompat.getColor(
                    itemView.context,
                    if (room.isActive) R.color.green_600 else R.color.orange_600
                )
            )
            courseMember.text = room.memberCount.toString()
            courseMember.visibility = if (room.showMemberCount) View.VISIBLE else View.GONE

            btnJoin.setOnClickListener {
                // xử lý khi click vào nút tham gia
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = roomList[position]
        holder.courseTitle.text = room.roomName
        holder.courseDescription.text = room.description
        holder.courseStatus.text = if (room.isActive) "Đang học" else "Đã dừng"

        // Xử lý trạng thái màu chữ status
        holder.courseStatus.setTextColor(
            ContextCompat.getColor(
                holder.itemView.context,
                if (room.isActive) R.color.green_600 else R.color.orange_600
            )
        )

        // Hiển thị các view theo điều kiện
        if (isJoinRoom) {
            holder.courseMember.visibility = View.VISIBLE
            holder.courseStatus.visibility= View.GONE
            holder.tvTime.visibility = View.GONE
            holder.tvMember.visibility = View.GONE
            holder.tvTimeCreate.visibility = View.VISIBLE
            holder.btnJoin.visibility = View.VISIBLE

        } else {
            holder.courseMember.visibility = View.GONE
            holder.tvTimeCreate.visibility = View.GONE
            holder.btnJoin.visibility = View.GONE
            holder.courseStatus.visibility= View.VISIBLE
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