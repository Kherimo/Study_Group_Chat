package com.example.studygroupchat.ui.fragments

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studygroupchat.R
import com.example.studygroupchat.adapter.RoomAdapter
import com.example.studygroupchat.model.Room
import com.example.studygroupchat.model.RoomWithExtras

class JoinRoomFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RoomAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_join_room, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recyclerView)
        adapter = RoomAdapter(getFakeRooms(),isJoinRoom = true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun getFakeRooms(): List<Room> {
        return listOf(
            Room("4", "Node.js Server", "Xây dựng backend API", "Ms. D", 10, true, false),
            Room("5", "Mobile Android", "Kotlin + Jetpack", "Mr. E", 18, true, false),
            Room("6", "Thiết kế UI/UX", "Figma + Prototype", "Ms. F", 7, false, false)
        )
    }
}