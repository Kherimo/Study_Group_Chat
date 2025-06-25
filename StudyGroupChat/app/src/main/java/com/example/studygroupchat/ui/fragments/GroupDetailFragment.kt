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
        }
        userViewModel.fetchCurrentUser()

        val btnBack = view.findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener { navigateHome() }

        val btnMembers = view.findViewById<ImageView>(R.id.btnMembers)
        val room = arguments?.getSerializable("room") as? Room
        val isOwner = arguments?.getBoolean("isOwner", false) == true
        if (isOwner) {
            btnMembers.visibility = View.VISIBLE
            btnMembers.setOnClickListener {
                val fragment = GroupManagerFragment()
                fragment.arguments = Bundle().apply { putSerializable("room", room) }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }


        if (room == null) {
            Toast.makeText(requireContext(), "Không tìm thấy thông tin nhóm", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }
        room?.let { r ->

            // Hiển thị thông tin
            view.findViewById<TextView>(R.id.tvgroupname).text = r.roomName
            view.findViewById<TextView>(R.id.tvTotalMember).text = r.members?.size?.toString() ?: "0"
            view.findViewById<TextView>(R.id.tvGroupDescription).text = r.description ?: "Không có mô tả"
            view.findViewById<TextView>(R.id.tvgroupDay).text = "Tạo ngày: ${formatCreatedAt(r.createdAt)}"

            view.findViewById<TextView>(R.id.tvTypeGroup).text = when (r.roomMode) {
                "public" -> "Nhóm công khai"
                "private" -> "Nhóm riêng tư"
                else -> "Không xác định"
            }

            val ivGroupImage = view.findViewById<ImageView>(R.id.ivGroupImage)
            if (!r.avatarUrl.isNullOrEmpty()) {
                Glide.with(requireContext())
                    .load(r.avatarUrl)
                    .placeholder(R.drawable.baseline_groups_24)
                    .into(ivGroupImage)
            }

            // ✅ Sửa lỗi ở đây — đưa vào trong `let`
            val roomMembers = r.members?.map {
                RoomMember(
                    roomId = r.roomId,
                    userId = it.userId,
                    joinedAt = "", // nếu API không trả joinedAt
                    user = it
                )
            } ?: emptyList()

        val myUserId = r.owner?.userId ?: 0
        val recyclerViewMember = view.findViewById<RecyclerView>(R.id.recyclerViewMember)

        val adapter = MemberAdapter(
            roomMembers,
            currentUserId = myUserId,
            onMoreClick = null,      // Không cần xử lý click trong Detail
            showMenu = false         // ❌ Ẩn nút menu 3 chấm
        )

        recyclerViewMember.adapter = adapter
        recyclerViewMember.layoutManager = LinearLayoutManager(requireContext())

            val shareButton = view.findViewById<MaterialButton>(R.id.shareGroup)
            shareButton.setOnClickListener {
                val fragment = ShareRoomFragment().apply {
                    arguments = Bundle().apply { putSerializable("room", r) }
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }

            val messageButton = view.findViewById<MaterialButton>(R.id.btnmess)
            messageButton.setOnClickListener {
                val intent = android.content.Intent(requireContext(), GroupChatActivity::class.java).apply {
                    putExtra("groupId", r.roomId.toString())
                    putExtra("groupName", r.roomName)
                    putExtra("groupMode", r.roomMode)
                    putExtra("memberCount", r.members?.size ?: 0)
                }
                startActivity(intent)
            }

            val joinButton = view.findViewById<MaterialButton>(R.id.btnjointhegroup)
            joinButton.setOnClickListener {
                val code = r.inviteCode
                if (!code.isNullOrBlank()) {
                    val userInfo = JitsiMeetUserInfo().apply { displayName = displayName }
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
            if (isOwner) {
                leaveButton.visibility = View.GONE
            } else {
                leaveButton.setOnClickListener {
                    viewModel.leaveRoom(r.roomId.toString())
                }
            }
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