package com.example.studygroupchat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studygroupchat.R
import com.example.studygroupchat.model.Group

class GroupAdapter(
    private val groupList: List<Group>,
    private val onClick: (Group) -> Unit
) : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupName: TextView = itemView.findViewById(R.id.tvGroupName)
        val lastMessage: TextView = itemView.findViewById(R.id.tvLastMessage)
        val avatar: ImageView = itemView.findViewById(R.id.imgGroupAvatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groupList[position]
        holder.groupName.text = group.name
        holder.lastMessage.text = group.lastMessage
        holder.itemView.setOnClickListener { onClick(group) }
    }

    override fun getItemCount() = groupList.size
}