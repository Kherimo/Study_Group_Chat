package com.example.studygroupchat.ui.fragments

import android.graphics.Bitmap
import android.graphics.Color
import android.provider.MediaStore
import android.content.ClipData
import android.content.ClipboardManager
import android.widget.Toast
import android.content.ContentValues
import android.os.Environment
import com.bumptech.glide.Glide
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.studygroupchat.R
import com.example.studygroupchat.model.room.Room
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

class ShareRoomFragment : Fragment() {
    private var room: Room? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        room = arguments?.getSerializable(ARG_ROOM) as? Room
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_share_room, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnBack = view.findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener { parentFragmentManager.popBackStack() }

        room?.let { r ->
            view.findViewById<TextView>(R.id.tvGroupTitle).text = r.roomName
            r.avatarUrl?.let {
                Glide.with(this)
                    .load(it)
                    .placeholder(R.drawable.baseline_groups_24)
                    .circleCrop()
                    .into(view.findViewById(R.id.imgGroupAvatar))
            }
            view.findViewById<TextView>(R.id.tvGroupCode).text = r.inviteCode ?: ""
            r.inviteCode?.let { code ->
                val link = "studygroupchat://join?code=$code"
                val bitmap = generateQrBitmap(link)
                view.findViewById<ImageView>(R.id.imgQRCode).setImageBitmap(bitmap)
                val btnDownload = view.findViewById<LinearLayout>(R.id.btnDownloadQR)
                btnDownload.setOnClickListener { saveQrImage(bitmap) }
                val btnCopy = view.findViewById<LinearLayout>(R.id.btnCopy)
                btnCopy.setOnClickListener {
                    copyInviteCode(code)
                }
            }
        }
    }

    private fun generateQrBitmap(content: String): Bitmap {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 400, 400)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bmp
    }

    private fun copyInviteCode(code: String) {
        val clipboard = requireContext().getSystemService(ClipboardManager::class.java)
        val clip = ClipData.newPlainText("Invite Code", code)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), getString(R.string.copied_invite_code), Toast.LENGTH_SHORT).show()
    }

    private fun saveQrImage(bitmap: Bitmap) {
        val filename = "qr_${System.currentTimeMillis()}"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$filename.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }
        val uri = requireActivity().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        uri?.let {
            requireActivity().contentResolver.openOutputStream(it)?.use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                Toast.makeText(requireContext(), getString(R.string.saved_qr_success), Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val ARG_ROOM = "room"
        fun newInstance(room: Room) = ShareRoomFragment().apply {
            arguments = Bundle().apply { putSerializable(ARG_ROOM, room) }
        }
    }
}