package com.example.studygroupchat.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import android.net.Uri
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.example.studygroupchat.R
import com.example.studygroupchat.adapter.RoomAdapter
import com.example.studygroupchat.api.ApiConfig
import com.example.studygroupchat.repository.RoomRepository
import com.example.studygroupchat.StudyGroupChatApplication
import com.example.studygroupchat.viewmodel.RoomViewModel
import com.example.studygroupchat.viewmodel.RoomViewModelFactory
import com.example.studygroupchat.ui.fragments.GroupDetailFragment
import com.google.android.material.button.MaterialButton

class JoinRoomFragment : Fragment() {

    private var inviteCodeArg: String? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RoomAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var editCodeRoom: EditText
    private lateinit var btnQR: MaterialButton

    private val scanLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            val contents = result.contents
            val code = if (contents.startsWith("studygroupchat://")) {
                Uri.parse(contents).getQueryParameter("code")
            } else contents
            code?.let {
                editCodeRoom.setText(it)
                viewModel.joinRoom(it)
            }
        }
    }
    private val viewModel: RoomViewModel by viewModels {
        val app = requireActivity().application as StudyGroupChatApplication
        RoomViewModelFactory(
            RoomRepository(
                ApiConfig.roomApiService,
                app.database.roomDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inviteCodeArg = arguments?.getString(ARG_INVITE_CODE)
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
        val btnJoin = view.findViewById<MaterialButton>(R.id.btnJoin)
        btnQR = view.findViewById(R.id.btnQR)

        adapter = RoomAdapter(
            emptyList(),
            isJoinRoom = true,
            onJoinClick = { room -> room.inviteCode?.let { viewModel.joinRoom(it) } }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        inviteCodeArg?.let {
            editCodeRoom.setText(it)
            viewModel.joinRoom(it)
        }

        btnJoin.setOnClickListener {
            val code = editCodeRoom.text.toString().trim()
            if (code.isNotBlank()) {
                viewModel.joinRoom(code)
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhập mã phòng", Toast.LENGTH_SHORT).show()
            }
        }

        btnQR.setOnClickListener {
            val options = ScanOptions()
            options.setPrompt(getString(R.string.scan_qr_prompt))
            options.setBeepEnabled(false)
            options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            scanLauncher.launch(options)
        }

        viewModel.rooms.observe(viewLifecycleOwner) { adapter.updateData(it) }
        viewModel.isLoading.observe(viewLifecycleOwner) { progressBar.visibility = if (it) View.VISIBLE else View.GONE }
        viewModel.joinResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Tham gia phòng thành công", Toast.LENGTH_SHORT).show()
                val fragment = GroupDetailFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable("room", it)
                        putString("roomId", it.roomId.toString())
                    }
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }.onFailure {
                Toast.makeText(requireContext(), "Không thể tham gia phòng", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.fetchPublicRooms()
    }

    companion object {
        private const val ARG_INVITE_CODE = "invite_code"
        fun newInstance(code: String) = JoinRoomFragment().apply {
            arguments = Bundle().apply { putString(ARG_INVITE_CODE, code) }
        }
    }
}