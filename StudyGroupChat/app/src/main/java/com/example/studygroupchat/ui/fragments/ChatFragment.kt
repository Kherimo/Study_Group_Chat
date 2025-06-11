package com.example.studygroupchat.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.studygroupchat.R

import android.content.Intent
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studygroupchat.adapter.GroupAdapter
import com.example.studygroupchat.api.ApiConfig
import com.example.studygroupchat.model.Group
import com.example.studygroupchat.repository.RoomRepository
import com.example.studygroupchat.viewmodel.RoomViewModel
import com.example.studygroupchat.viewmodel.RoomViewModelFactory

class ChatFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var groupAdapter: GroupAdapter
    private val groupList = mutableListOf<Group>()

    private val viewModel: RoomViewModel by viewModels {
        RoomViewModelFactory(RoomRepository(ApiConfig.roomApiService))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        recyclerView = view.findViewById(R.id.recyclerGroupList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        groupAdapter = GroupAdapter(groupList) { group ->
            val intent = Intent(requireContext(), GroupChatActivity::class.java)
            intent.putExtra("groupId", group.id)
            intent.putExtra("groupName", group.name)
            startActivity(intent)
        }

        recyclerView.adapter = groupAdapter

        viewModel.rooms.observe(viewLifecycleOwner) { rooms ->
            groupList.clear()
            groupList.addAll(rooms.map { room ->
                Group(room.roomId.toString(), room.roomName, room.description ?: "")
            })
            groupAdapter.notifyDataSetChanged()
        }


        viewModel.fetchMyRooms()

        return view
    }
}
