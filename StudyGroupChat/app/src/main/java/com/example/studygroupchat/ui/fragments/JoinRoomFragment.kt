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
import com.example.studygroupchat.model.room.Room
import com.example.studygroupchat.model.user.User

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
        val ownerD = User(
            userId = 4,
            userName = "ms.d",
            fullName = "Ms. D",
            email = "d@example.com",
            phoneNumber = "0123456701",
            passwordHash = "",
            avatarUrl = null,
            createdAt = "2025-06-01T10:00:00"
        )

        val ownerE = User(
            userId = 5,
            userName = "mr.e",
            fullName = "Mr. E",
            email = "e@example.com",
            phoneNumber = "0123456702",
            passwordHash = "",
            avatarUrl = null,
            createdAt = "2025-06-01T10:00:00"
        )

        val ownerF = User(
            userId = 6,
            userName = "ms.f",
            fullName = "Ms. F",
            email = "f@example.com",
            phoneNumber = "0123456703",
            passwordHash = "",
            avatarUrl = null,
            createdAt = "2025-06-01T10:00:00"
        )

        return listOf(
            Room(
                roomId = 4,
                ownerId = ownerD.userId,
                roomName = "Node.js Server",
                description = "Xây dựng backend API",
                inviteCode = "NODE123",
                expiredAt = "2025-12-31T23:59:59",
                createdAt = "2025-06-01T10:00:00",
                owner = ownerD,
                members = listOf()
            ),
            Room(
                roomId = 5,
                ownerId = ownerE.userId,
                roomName = "Mobile Android",
                description = "Kotlin + Jetpack",
                inviteCode = "ANDROID456",
                expiredAt = "2025-11-30T23:59:59",
                createdAt = "2025-06-01T10:00:00",
                owner = ownerE,
                members = listOf()
            ),
            Room(
                roomId = 6,
                ownerId = ownerF.userId,
                roomName = "Thiết kế UI/UX",
                description = "Figma + Prototype",
                inviteCode = "UX789",
                expiredAt = "2025-10-15T23:59:59",
                createdAt = "2025-06-01T10:00:00",
                owner = ownerF,
                members = listOf()
            )
        )
    }

}