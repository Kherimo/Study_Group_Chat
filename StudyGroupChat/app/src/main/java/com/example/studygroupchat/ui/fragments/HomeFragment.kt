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

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RoomAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recyclerView)
        adapter = RoomAdapter(getFakeRooms(), isJoinRoom = false)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
            ) {
                outRect.top = 10 // khoảng cách giữa các item
                outRect.bottom = 10
            }
        })
    }



    private fun getFakeRooms(): List<Room> {
        val ownerA = User(
            userId = 1,
            userName = "mr.a",
            fullName = "Mr. A",
            email = "a@example.com",
            phoneNumber = "0123456789",
            passwordHash = "",
            avatarUrl = null,
            createdAt = "2025-06-01T10:00:00"
        )

        val ownerB = User(
            userId = 2,
            userName = "ms.b",
            fullName = "Ms. B",
            email = "b@example.com",
            phoneNumber = "0123456790",
            passwordHash = "",
            avatarUrl = null,
            createdAt = "2025-06-01T10:00:00"
        )

        val ownerC = User(
            userId = 3,
            userName = "mr.c",
            fullName = "Mr. C",
            email = "c@example.com",
            phoneNumber = "0123456791",
            passwordHash = "",
            avatarUrl = null,
            createdAt = "2025-06-01T10:00:00"
        )

        return listOf(
            Room(
                roomId = 1,
                ownerId = ownerA.userId,
                roomName = "Lập trình Java",
                description = "Học Java cơ bản đến nâng cao",
                inviteCode = "ABC123",
                expiredAt = "2025-12-31T23:59:59",
                createdAt = "2025-06-01T10:00:00",
                owner = ownerA,
                members = listOf()
            ),
            Room(
                roomId = 2,
                ownerId = ownerB.userId,
                roomName = "Web Frontend",
                description = "HTML, CSS, JS, React",
                inviteCode = "XYZ456",
                expiredAt = "2025-11-30T23:59:59",
                createdAt = "2025-06-01T10:00:00",
                owner = ownerB,
                members = listOf()
            ),
            Room(
                roomId = 3,
                ownerId = ownerC.userId,
                roomName = "Python Cơ bản",
                description = "Từ cơ bản đến ứng dụng",
                inviteCode = "PY789",
                expiredAt = "2025-10-15T23:59:59",
                createdAt = "2025-06-01T10:00:00",
                owner = ownerC,
                members = listOf()
            )
        )
    }

}