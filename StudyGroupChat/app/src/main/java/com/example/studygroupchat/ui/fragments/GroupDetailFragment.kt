package com.example.studygroupchat.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.studygroupchat.R
import com.example.studygroupchat.adapter.MemberAdapter
import com.example.studygroupchat.model.room.Room
import com.example.studygroupchat.model.room.RoomMember
import com.google.android.material.appbar.MaterialToolbar
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GroupDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_group_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbargroudDetail)

        toolbar.setNavigationIcon(R.drawable.ic_arrow_left) // icon quay lại
        toolbar.title = "Chi tiết nhóm"

        toolbar.setNavigationOnClickListener {
            // Quay lại Fragment trước đó
            parentFragmentManager.popBackStack()
        }

        toolbar.inflateMenu(R.menu.toolbar_menu)

        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_members -> {
                    val fragment = GroupManagerFragment()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment) // container ID trong Activity
                        .addToBackStack(null)
                        .commit()
                    true
                }
                else -> false
            }
        }


        val room = arguments?.getSerializable("room") as? Room
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
                    .placeholder(R.drawable.baseline_account_circle_24)
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
