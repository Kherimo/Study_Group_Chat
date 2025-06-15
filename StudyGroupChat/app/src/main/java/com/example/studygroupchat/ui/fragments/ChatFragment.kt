package com.example.studygroupchat.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.studygroupchat.R

import android.content.Intent
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studygroupchat.adapter.GroupAdapter
import com.example.studygroupchat.api.ApiConfig
import com.example.studygroupchat.model.Group
import com.example.studygroupchat.repository.RoomRepository
import com.example.studygroupchat.repository.RoomMessageRepository
import com.example.studygroupchat.viewmodel.RoomViewModel
import com.example.studygroupchat.viewmodel.RoomViewModelFactory
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ChatFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var groupAdapter: GroupAdapter
    private val groupList = mutableListOf<Group>()
    private val messageRepo = RoomMessageRepository(ApiConfig.roomMessageApiService)

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
            viewLifecycleOwner.lifecycleScope.launch {
                groupList.clear()
                for (room in rooms) {
                    val lastResult = messageRepo.getLastRoomMessage(room.roomId.toString())
                    val last = lastResult.getOrNull()
                    val timeText = formatRelativeTime(last?.sentAt)
                    groupList.add(
                        Group(
                            room.roomId.toString(),
                            room.roomName,
                            last?.content ?: "",
                            room.avatarUrl,
                            timeText
                        )
                    )
                }
                groupAdapter.notifyDataSetChanged()
            }
        }


        viewModel.fetchMyRooms()

        return view
    }

    private fun formatRelativeTime(raw: String?): String {
        if (raw.isNullOrBlank()) return ""
        return try {
            val instant = try {
                Instant.parse(raw)
            } catch (e: Exception) {
                LocalDateTime.parse(raw, DateTimeFormatter.ISO_DATE_TIME)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
            }

            val diff = Duration.between(instant, Instant.now())
            val minutes = diff.toMinutes()
            when {
                minutes < 60 -> "${minutes} phút"
                minutes < 1440 -> "${minutes / 60} giờ"
                else -> "${minutes / 1440} ngày"
            }
        } catch (e: Exception) {
            ""
        }
    }
}