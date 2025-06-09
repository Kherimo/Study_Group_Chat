package com.example.studygroupchat.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studygroupchat.R
import com.example.studygroupchat.adapter.RoomAdapter
import com.example.studygroupchat.api.ApiConfig
import com.example.studygroupchat.repository.RoomRepository
import com.example.studygroupchat.viewmodel.RoomViewModel
import com.example.studygroupchat.viewmodel.RoomViewModelFactory

class JoinRoomFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RoomAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var editCodeRoom: EditText
    private val viewModel: RoomViewModel by viewModels {
        RoomViewModelFactory(RoomRepository(ApiConfig.roomApiService))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_join_room, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        editCodeRoom = view.findViewById(R.id.editcodeRoom)
        val btnJoin = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnJoin)

        adapter = RoomAdapter(emptyList(), isJoinRoom = true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        btnJoin.setOnClickListener {
            val code = editCodeRoom.text.toString().trim()
            if (code.isNotBlank()) {
                viewModel.joinRoom(code)
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhập mã phòng", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.rooms.observe(viewLifecycleOwner) { adapter.updateData(it) }
        viewModel.isLoading.observe(viewLifecycleOwner) { progressBar.visibility = if (it) View.VISIBLE else View.GONE }
        viewModel.joinResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Tham gia phòng thành công", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(requireContext(), "Không thể tham gia phòng", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.fetchMyRooms()
    }
}