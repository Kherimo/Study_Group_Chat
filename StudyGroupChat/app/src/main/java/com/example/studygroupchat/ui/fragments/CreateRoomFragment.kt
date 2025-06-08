package com.example.studygroupchat.ui.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.studygroupchat.R
import com.example.studygroupchat.api.ApiConfig
import com.example.studygroupchat.databinding.FragmentCreateRoomBinding
import com.example.studygroupchat.viewmodel.CreateRoomViewModel
import com.example.studygroupchat.viewmodel.CreateRoomViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CreateRoomFragment : Fragment() {
    private var _binding: FragmentCreateRoomBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreateRoomViewModel by viewModels { 
        CreateRoomViewModelFactory(ApiConfig.roomApiService)
    }
    private var selectedDate: Date? = null

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


        viewModel.createRoom(roomName, description, expiredAt)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.createRoomResult.observe(viewLifecycleOwner) { result ->
                result.fold(
                    onSuccess = { room ->
                        Toast.makeText(requireContext(), "Tạo phòng thành công", Toast.LENGTH_SHORT).show()
                        requireActivity().onBackPressed()
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