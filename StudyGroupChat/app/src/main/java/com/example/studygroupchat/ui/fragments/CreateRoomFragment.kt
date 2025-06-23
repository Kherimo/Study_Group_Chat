package com.example.studygroupchat.ui.fragments

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.studygroupchat.R
import com.example.studygroupchat.api.ApiConfig
import com.example.studygroupchat.databinding.FragmentCreateRoomBinding
import com.example.studygroupchat.ui.fragments.GroupDetailFragment
import com.example.studygroupchat.viewmodel.CreateRoomViewModel
import com.example.studygroupchat.viewmodel.CreateRoomViewModelFactory
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CreateRoomFragment : Fragment() {
    private var _binding: FragmentCreateRoomBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreateRoomViewModel by viewModels {
        CreateRoomViewModelFactory(ApiConfig.roomApiService)
    }
    private var selectedDate: Date? = null
    private var selectedPart: MultipartBody.Part? = null
    private var inviteCode: String = generateInviteCode()

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { handleImageSelected(it) }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateInviteCodeDisplay()
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnClose.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.editdate.setOnClickListener {
            showDatePicker()
        }

        binding.imgRoomAvatar.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.tvChangePhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.tvCopyCode.setOnClickListener {
            copyInviteCode()
        }

        binding.ivChangeCode.setOnClickListener {
            inviteCode = generateInviteCode()
            updateInviteCodeDisplay()
        }

        binding.btCreateRoom.setOnClickListener {
            createRoom()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                selectedDate = calendar.time
                updateDateDisplay()
            },
            year,
            month,
            day
        ).show()
    }

    private fun updateDateDisplay() {
        selectedDate?.let { date ->
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.editdate.setText(dateFormat.format(date))
        }
    }

    private fun handleImageSelected(uri: Uri) {
        binding.imgRoomAvatar.setImageURI(uri)
        val file = File(requireContext().cacheDir, "room_avatar")
        val input = requireContext().contentResolver.openInputStream(uri)
        file.outputStream().use { out ->
            input?.copyTo(out)
        }
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        selectedPart = MultipartBody.Part.createFormData("avatar", file.name, requestBody)
    }

    private fun generateInviteCode(length: Int = 8): String {
        val chars = (('A'..'Z') + ('0'..'9')).joinToString("")
        return (1..length).map { chars.random() }.joinToString("")
    }

    private fun updateInviteCodeDisplay() {
        binding.tvCode.text = inviteCode
    }

    private fun createRoom() {
        val roomName = binding.editNameRoom.text.toString()
        val description = binding.editdescribe.text.toString()
        val expiredAt = selectedDate?.let { viewModel.formatDate(it) }

        if (roomName.isBlank()) {
            Toast.makeText(requireContext(), "Vui lòng nhập tên phòng", Toast.LENGTH_SHORT).show()
            return
        }

        if (description.isBlank()) {
            Toast.makeText(requireContext(), "Vui lòng nhập mô tả phòng", Toast.LENGTH_SHORT).show()
            return
        }

        if (expiredAt.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Vui lòng chọn ngày hết hạn", Toast.LENGTH_SHORT).show()
            return
        }

        val roomMode = if (binding.radioPublic.isChecked) "public" else "private"

        viewModel.createRoom(selectedPart, roomName, description, roomMode, expiredAt)
    }

    private fun copyInviteCode() {
        val clipboard = requireContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Invite Code", inviteCode)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), "Đã sao chép mã mời", Toast.LENGTH_SHORT).show()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.createRoomResult.observe(viewLifecycleOwner) { result ->
                result.fold(
                    onSuccess = { room ->
                        Toast.makeText(requireContext(), "Tạo phòng thành công", Toast.LENGTH_SHORT).show()
                        val fragment = GroupDetailFragment().apply {
                            arguments = Bundle().apply {
                                putSerializable("room", room)
                                putString("roomId", room.roomId.toString())
                                putBoolean("isOwner", true)
                            }
                        }
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit()
                    },
                    onFailure = { error ->
                        Toast.makeText(requireContext(), "Vui lòng kiểm tra mạng hoặc server không sẵn sàng.", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}