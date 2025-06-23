package com.example.studygroupchat.ui.fragments

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.studygroupchat.R
import com.example.studygroupchat.model.room.Room
import com.google.android.material.appbar.MaterialToolbar
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
        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbarShareLinkRoom)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left)
        toolbar.setNavigationOnClickListener { parentFragmentManager.popBackStack() }

        room?.let { r ->
            view.findViewById<TextView>(R.id.tvGroupTitle).text = r.roomName
            view.findViewById<TextView>(R.id.tvGroupCode).text = r.inviteCode ?: ""
            r.inviteCode?.let { code ->
                val link = "studygroupchat://join?code=$code"
                val bitmap = generateQrBitmap(link)
                view.findViewById<ImageView>(R.id.imgQRCode).setImageBitmap(bitmap)
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

    companion object {
        private const val ARG_ROOM = "room"
        fun newInstance(room: Room) = ShareRoomFragment().apply {
            arguments = Bundle().apply { putSerializable(ARG_ROOM, room) }
        }
    }
}