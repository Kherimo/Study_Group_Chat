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
import com.example.studygroupchat.repository.UserRepository
import com.example.studygroupchat.StudyGroupChatApplication
import com.example.studygroupchat.viewmodel.RoomViewModel
import com.example.studygroupchat.viewmodel.RoomViewModelFactory
import com.example.studygroupchat.viewmodel.UserViewModel
import com.example.studygroupchat.viewmodel.UserViewModelFactory
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
    private lateinit var messageRepository: RoomMessageRepository
    private var currentUserId: Int = 0

    private val viewModel: RoomViewModel by viewModels {
        val app = requireActivity().application as StudyGroupChatApplication
        RoomViewModelFactory(
            RoomRepository(
                ApiConfig.roomApiService,
                app.database.roomDao()
            )
        )
    }

    private val userViewModel: UserViewModel by viewModels {
        val app = requireActivity().application as StudyGroupChatApplication
        UserViewModelFactory(
            UserRepository(
                ApiConfig.userApiService,
                app.database.userDao()
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        val app = requireActivity().application as StudyGroupChatApplication
        messageRepository = RoomMessageRepository(
            ApiConfig.roomMessageApiService,
            app.database.messageDao()
        )

        recyclerView = view.findViewById(R.id.recyclerGroupList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        groupAdapter = GroupAdapter(groupList) { group ->
            val intent = Intent(requireContext(), GroupChatActivity::class.java)
            intent.putExtra("groupId", group.id)
            intent.putExtra("groupName", group.name)
            intent.putExtra("groupMode", group.roomMode)
            intent.putExtra("memberCount", group.memberCount)
            startActivity(intent)
        }

        recyclerView.adapter = groupAdapter

        userViewModel.user.observe(viewLifecycleOwner) { user ->
            currentUserId = user.userId
        }
        userViewModel.fetchCurrentUser()

        viewModel.rooms.observe(viewLifecycleOwner) { rooms ->
            viewLifecycleOwner.lifecycleScope.launch {
                groupList.clear()
                for (room in rooms) {
                    var last = room.latestMessage
                    if (last == null) {
                        last = messageRepository.getLastRoomMessage(room.roomId.toString()).getOrNull()
                    }
                    val timeText = formatRelativeTime(last?.sentAt)
                    val timestamp = parseTimestamp(last?.sentAt)
                    val senderName = when {
                        last?.senderId == currentUserId -> "Bạn"
                        else -> last?.sender?.fullName ?: last?.sender?.userName ?: ""
                    }
                    val messageText = if (last != null && senderName.isNotEmpty()) {
                        "$senderName: ${last.content ?: ""}"
                    } else {
                        last?.content ?: ""
                    }
                    groupList.add(
                        Group(
                            room.roomId.toString(),
                            room.roomName,
                            messageText,
                            room.avatarUrl,
                            timeText,
                            timestamp,
                            room.roomMode,
                            room.members?.size ?: 0
                        )
                    )
                }
                groupList.sortByDescending { it.lastMessageTimestamp ?: Long.MIN_VALUE }
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

    private fun parseTimestamp(raw: String?): Long? {
        if (raw.isNullOrBlank()) return null
        return try {
            val instant = try {
                Instant.parse(raw)
            } catch (e: Exception) {
                LocalDateTime.parse(raw, DateTimeFormatter.ISO_DATE_TIME)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
            }
            instant.toEpochMilli()
        } catch (e: Exception) {
            null
        }
    }
}