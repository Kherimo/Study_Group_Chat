package com.example.studygroupchat.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.studygroupchat.R

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studygroupchat.adapter.GroupAdapter
import com.example.studygroupchat.model.Group

class ChatFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var groupAdapter: GroupAdapter
    private val groupList = mutableListOf<Group>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        recyclerView = view.findViewById(R.id.recyclerGroupList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Dummy data – thay bằng dữ liệu từ server nếu cần
        groupList.add(Group("1", "Gia đình", "Mẹ: Ăn cơm chưa con?"))
        groupList.add(Group("2", "Bạn bè", "Hùng: Tối đi chơi không?"))
        groupList.add(Group("3", "Công ty", "Sếp: Sáng mai họp lúc 9h"))

        groupAdapter = GroupAdapter(groupList) { group ->
            val intent = Intent(requireContext(), GroupChatActivity::class.java)
            intent.putExtra("groupId", group.id)
            intent.putExtra("groupName", group.name)
            startActivity(intent)
        }

        recyclerView.adapter = groupAdapter

        return view
    }
}
