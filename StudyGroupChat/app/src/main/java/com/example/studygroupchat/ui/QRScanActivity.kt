package com.example.studygroupchat.ui

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.studygroupchat.R
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.RGBLuminanceSource
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import java.lang.Exception

class QRScanActivity : AppCompatActivity() {
    private lateinit var capture: CaptureManager
    private lateinit var barcodeView: DecoratedBarcodeView

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data ?: return@registerForActivityResult
            decodeFromGallery(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_scan)
        barcodeView = findViewById(R.id.barcode_scanner)
        capture = CaptureManager(this, barcodeView)
        capture.initializeFromIntent(intent, savedInstanceState)
        capture.decode()

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<ImageView>(R.id.btnGallery).setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            galleryLauncher.launch(intent)
        }
    }

    private fun decodeFromGallery(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = inputStream?.use { BitmapFactory.decodeStream(it) }
        bitmap?.let { bmp ->
            val intArray = IntArray(bmp.width * bmp.height)
            bmp.getPixels(intArray, 0, bmp.width, 0, 0, bmp.width, bmp.height)
            val source = RGBLuminanceSource(bmp.width, bmp.height, intArray)
            val binary = BinaryBitmap(HybridBinarizer(source))
            try {
                val result = MultiFormatReader().decode(binary)
                val data = Intent().apply { putExtra("SCAN_RESULT", result.text) }
                setResult(Activity.RESULT_OK, data)
                finish()
            } catch (e: Exception) {
                Toast.makeText(this, getString(R.string.no_qr_found), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        capture.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        capture.onSaveInstanceState(outState)
    }
}