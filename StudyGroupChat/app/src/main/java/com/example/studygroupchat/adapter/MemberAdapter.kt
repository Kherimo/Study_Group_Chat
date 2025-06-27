package com.example.studygroupchat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studygroupchat.R
import com.example.studygroupchat.model.room.RoomMember

class MemberAdapter(
    private var members: List<RoomMember>,
    private val currentUserId: Int? = null, // ID người dùng hiện tại để gắn nhãn "Bạn"
    private val ownerId: Int? = null, // ID chủ phòng để hiển thị "Admin"
    private val onMoreClick: ((RoomMember, View) -> Unit)? = null,
    private val showMenu: Boolean = true
) : RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

    inner class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAvatar: TextView = itemView.findViewById(R.id.tvAvatar)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        val tvRole: TextView = itemView.findViewById(R.id.tvRole)
        val btnMore: ImageButton = itemView.findViewById(R.id.btnMore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_member, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val member = members[position]
        val user = member.user

        if (user != null) {
            val initials = user.fullName?.split(" ")?.map { it.firstOrNull()?.uppercaseChar() ?: "" }?.joinToString("") ?: ""
            holder.tvAvatar.text = initials.take(2)
            holder.tvName.text = user.fullName ?: user.userName
            holder.tvEmail.text = user.email
        }

        // Tạm mặc định "Admin" nếu là chủ phòng
        if (user?.userId == members.firstOrNull()?.user?.userId) {
            holder.tvRole.text = "Admin"
        } else {
            holder.tvRole.text = "Thành viên"
        }

        if (showMenu && user?.userId != ownerId) {
            holder.btnMore.visibility = View.VISIBLE
            holder.btnMore.setOnClickListener { v ->
                onMoreClick?.invoke(member, v)
            }
        } else {
            holder.btnMore.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = members.size

    fun updateData(newList: List<RoomMember>) {
        members = newList
        notifyDataSetChanged()
    }
}