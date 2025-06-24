package com.example.studygroupchat.ui.fragments

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.RadioButton
import android.widget.Toast
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.studygroupchat.R
import com.example.studygroupchat.StudyGroupChatApplication
import com.example.studygroupchat.api.ApiConfig
import com.example.studygroupchat.model.room.Room
import com.example.studygroupchat.repository.RoomRepository
import com.example.studygroupchat.viewmodel.ManageRoomViewModel
import com.example.studygroupchat.viewmodel.ManageRoomViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.bumptech.glide.Glide
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class GroupManagerFragment : Fragment() {

    private lateinit var editName: EditText
    private lateinit var editDescription: EditText
    private lateinit var radioPublic: RadioButton
    private lateinit var radioPrivate: RadioButton
    private lateinit var imgAvatar: ImageView
    private lateinit var imgChange: ImageView
    private var selectedPart: MultipartBody.Part? = null
    private lateinit var tvExpireDate: TextView
    private lateinit var btnExtend: TextView
    private var selectedDate: Date? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { handleImageSelected(it) }
        }

    private val viewModel: ManageRoomViewModel by viewModels {
        val app = requireActivity().application as StudyGroupChatApplication
        ManageRoomViewModelFactory(
            RoomRepository(
                ApiConfig.roomApiService,
                app.database.roomDao()
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_group_manager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left)
        toolbar.title = "Quản lý nhóm"
        toolbar.inflateMenu(R.menu.menu_group_manager)
        toolbar.setNavigationOnClickListener { parentFragmentManager.popBackStack() }

        editName = view.findViewById(R.id.editGroupName)
        editDescription = view.findViewById(R.id.editDescription)
        radioPublic = view.findViewById(R.id.radioPublic)
        radioPrivate = view.findViewById(R.id.radioPrivate)
        imgAvatar = view.findViewById(R.id.imgRoomAvatar)
        imgChange = view.findViewById(R.id.imgChangeAvatar)
        tvExpireDate = view.findViewById(R.id.tvExpireDate)
        btnExtend = view.findViewById(R.id.btnExtend)
        val tvType = view.findViewById<TextView>(R.id.tvTypeGroup)
        val tvMember = view.findViewById<TextView>(R.id.tvTotalMember)

        val room = arguments?.getSerializable("room") as? Room
        if (room != null) {
            editName.setText(room.roomName)
            editDescription.setText(room.description)
            when (room.roomMode) {
                "public" -> radioPublic.isChecked = true
                "private" -> radioPrivate.isChecked = true
            }
            tvType.text = if (room.roomMode == "public") "Nhóm công khai" else "Nhóm riêng tư"
            tvMember.text = room.members?.size?.toString() ?: "0"
            room.avatarUrl?.let {
                Glide.with(this)
                    .load(it)
                    .placeholder(R.drawable.baseline_groups_24)
                    .into(imgAvatar)
            }
            selectedDate = parseApiDate(room.expiredAt)
            updateDateDisplay()
        }

        imgAvatar.setOnClickListener { pickImageLauncher.launch("image/*") }
        imgChange.setOnClickListener { pickImageLauncher.launch("image/*") }

        radioPublic.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                radioPrivate.isChecked = false
                tvType.text = "Nhóm công khai"
            }
        }
        radioPrivate.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                radioPublic.isChecked = false
                tvType.text = "Nhóm riêng tư"
            }
        }

        btnExtend.setOnClickListener { showDatePicker() }
        tvExpireDate.setOnClickListener { showDatePicker() }

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_save -> {
                    val id = room?.roomId?.toString() ?: return@setOnMenuItemClickListener true
                    val mode = if (radioPublic.isChecked) "public" else "private"
                    val expired = selectedDate?.let { formatDateForApi(it) } ?: room?.expiredAt
                    viewModel.updateRoom(
                        id,
                        selectedPart,
                        editName.text.toString(),
                        editDescription.text.toString(),
                        mode,
                        expired
                    )
                    true
                }
                R.id.action_delete -> {
                    val id = room?.roomId?.toString() ?: return@setOnMenuItemClickListener true
                    viewModel.deleteRoom(id)
                    true
                }
                else -> false
            }
        }

        viewModel.updateResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Đã cập nhật nhóm", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
            result.onFailure {
                Toast.makeText(requireContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.deleteResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Đã xoá nhóm", Toast.LENGTH_SHORT).show()
                navigateHome()
            }
            result.onFailure {
                Toast.makeText(requireContext(), "Xoá nhóm thất bại", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleImageSelected(uri: Uri) {
        imgAvatar.setImageURI(uri)
        val file = File(requireContext().cacheDir, "room_avatar")
        val input = requireContext().contentResolver.openInputStream(uri)
        file.outputStream().use { out ->
            input?.copyTo(out)
        }
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        selectedPart = MultipartBody.Part.createFormData("avatar", file.name, requestBody)
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        selectedDate?.let { calendar.time = it }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(requireContext(), { _, y, m, d ->
            calendar.set(y, m, d)
            selectedDate = calendar.time
            updateDateDisplay()
        }, year, month, day).show()
    }

    private fun updateDateDisplay() {
        selectedDate?.let { date ->
            val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            tvExpireDate.text = "Nhóm sẽ hết hạn vào ${displayFormat.format(date)}"
        }
    }

    private fun parseApiDate(raw: String?): Date? {
        if (raw.isNullOrBlank()) return null
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            format.parse(raw)
        } catch (e: Exception) {
            null
        }
    }

    private fun formatDateForApi(date: Date): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        return format.format(date)
    }

    private fun navigateHome() {
        parentFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment())
            .commit()
    }
}