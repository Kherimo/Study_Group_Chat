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
        return listOf(
            Room("1", "Lập trình Java", "Học Java cơ bản đến nâng cao", "Mr. A", 12, true, true),
            Room("2", "Web Frontend", "HTML, CSS, JS, React", "Ms. B", 24, true, true),
            Room("3", "Python Cơ bản", "Từ cơ bản đến ứng dụng", "Mr. C", 15, false, true)
        )
    }
}
