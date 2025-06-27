package com.example.studygroupchat.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.studygroupchat.R
import com.example.studygroupchat.adapter.MemberAdapter
import com.example.studygroupchat.api.ApiConfig
import com.example.studygroupchat.model.room.Room
import com.example.studygroupchat.model.room.RoomMember
import com.example.studygroupchat.repository.RoomRepository
import com.example.studygroupchat.repository.UserRepository
import com.example.studygroupchat.StudyGroupChatApplication
import com.example.studygroupchat.viewmodel.RoomViewModel
import com.example.studygroupchat.viewmodel.RoomViewModelFactory
import com.example.studygroupchat.viewmodel.UserViewModel
import com.example.studygroupchat.viewmodel.UserViewModelFactory
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetUserInfo
import com.google.android.material.button.MaterialButton
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GroupDetailFragment : Fragment() {

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

    private var currentUserId: Int = 0
    private lateinit var recyclerViewMember: RecyclerView
    private var memberAdapter: MemberAdapter? = null
    private var members: List<RoomMember> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_group_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var displayName = ""
        userViewModel.user.observe(viewLifecycleOwner) { user ->
            displayName = user.fullName ?: user.userName
            currentUserId = user.userId
        }
        userViewModel.fetchCurrentUser()

        val btnBack = view.findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener { navigateHome() }

        val btnMembers = view.findViewById<ImageView>(R.id.btnMembers)

        recyclerViewMember = view.findViewById(R.id.recyclerViewMember)
        recyclerViewMember.layoutManager = LinearLayoutManager(requireContext())

        val roomId = arguments?.getString("roomId")
        if (roomId == null) {
            Toast.makeText(requireContext(), "Không tìm thấy thông tin nhóm", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        viewModel.selectedRoom.observe(viewLifecycleOwner) { result ->
            result.onSuccess { room ->
                btnMembers.visibility = if (room.ownerId == currentUserId) View.VISIBLE else View.GONE
                btnMembers.setOnClickListener {
                    val fragment = GroupManagerFragment()
                    fragment.arguments = Bundle().apply { putSerializable("room", room) }
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()
                }
                updateRoomInfo(view, room, displayName)
            }
        }

        viewModel.members.observe(viewLifecycleOwner) { list ->
            members = list
            refreshAdapter()
            view.findViewById<TextView>(R.id.tvTotalMember).text = list.size.toString()
        }

        viewModel.leaveResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Đã rời nhóm", Toast.LENGTH_SHORT).show()
                navigateHome()
            }
            result.onFailure {
                Toast.makeText(requireContext(), "Rời nhóm thất bại", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.getRoom(roomId)
        viewModel.fetchRoomMembers(roomId)
    }

    private fun updateRoomInfo(view: View, room: Room, displayName: String) {
        view.findViewById<TextView>(R.id.tvgroupname).text = room.roomName
        view.findViewById<TextView>(R.id.tvGroupDescription).text = room.description ?: "Không có mô tả"
        view.findViewById<TextView>(R.id.tvgroupDay).text = "Tạo ngày: ${formatCreatedAt(room.createdAt)}"
        view.findViewById<TextView>(R.id.tvTypeGroup).text = when (room.roomMode) {
            "public" -> "Nhóm công khai"
            "private" -> "Nhóm riêng tư"
            else -> "Không xác định"
        }

        val ivGroupImage = view.findViewById<ImageView>(R.id.ivGroupImage)
        if (!room.avatarUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(room.avatarUrl)
                .placeholder(R.drawable.baseline_groups_24)
                .into(ivGroupImage)
        }

        refreshAdapter()

        view.findViewById<MaterialButton>(R.id.shareGroup).setOnClickListener {
            val fragment = ShareRoomFragment().apply {
                arguments = Bundle().apply { putSerializable("room", room) }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<MaterialButton>(R.id.btnmess).setOnClickListener {
            val intent = android.content.Intent(requireContext(), GroupChatActivity::class.java).apply {
                putExtra("groupId", room.roomId.toString())
                putExtra("groupName", room.roomName)
                putExtra("groupMode", room.roomMode)
                putExtra("memberCount", room.members?.size ?: members.size)
            }
            startActivity(intent)
        }

        view.findViewById<MaterialButton>(R.id.btnjointhegroup).setOnClickListener {
            val code = room.inviteCode
            if (!code.isNullOrBlank()) {
                val userInfo = JitsiMeetUserInfo().apply { this.displayName = displayName }
                val options = JitsiMeetConferenceOptions.Builder()
                    .setRoom(code)
                    .setUserInfo(userInfo)
                    .setFeatureFlag("welcomepage.enabled", false)
                    .setFeatureFlag("prejoinpage.enabled", false)
                    .build()
                JitsiMeetActivity.launch(requireContext(), options)
            } else {
                Toast.makeText(requireContext(), "Không tìm thấy mã phòng", Toast.LENGTH_SHORT).show()
            }
        }

        val leaveButton = view.findViewById<MaterialButton>(R.id.leavetheGroup)
        if (room.ownerId == currentUserId) {
            leaveButton.visibility = View.GONE
        } else {
            leaveButton.setOnClickListener {
                viewModel.leaveRoom(room.roomId.toString())
            }
        }
    }

    private fun refreshAdapter() {
        if (memberAdapter == null) {
            memberAdapter = MemberAdapter(
                members,
                currentUserId = currentUserId,
                ownerId = viewModel.selectedRoom.value?.getOrNull()?.ownerId ?: 0,
                onMoreClick = null,
                showMenu = false
            )
            recyclerViewMember.adapter = memberAdapter
        } else {
            memberAdapter?.updateData(members)
        }
    }
    fun formatCreatedAt(raw: String?): String {
        if (raw.isNullOrBlank()) return "Không rõ"
        return try {
            val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
            val outputFormat = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
            val date = inputFormat.parse(raw)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            "Không rõ"
        }
    }

    private fun navigateHome() {
        parentFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment())
            .commit()
    }

//    private fun showMemberOptions(member: RoomMember) {
//        val popup = PopupMenu(requireContext(), requireView().findViewById(R.id.recyclerViewMember))
//        popup.menuInflater.inflate(R.menu.member_options_menu, popup.menu)
//
//        popup.setOnMenuItemClickListener { item ->
//            when (item.itemId) {
//                R.id.action_view_profile -> {
//                    Toast.makeText(requireContext(), "Xem hồ sơ: ${member.user?.fullName}", Toast.LENGTH_SHORT).show()
//                    true
//                }
//                R.id.action_remove -> {
//                    Toast.makeText(requireContext(), "Xóa thành viên", Toast.LENGTH_SHORT).show()
//                    true
//                }
//                else -> false
//            }
//        }
//
//        popup.show()
//    }

}