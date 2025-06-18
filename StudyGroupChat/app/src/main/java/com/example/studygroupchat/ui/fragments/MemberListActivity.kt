package com.example.studygroupchat.ui.fragments

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.studygroupchat.R
import com.google.android.material.appbar.MaterialToolbar

class MemberListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_member_list)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

// Set tiêu đề và phụ đề tại đây (không dùng XML nữa)
        supportActionBar?.apply {
            title = "Thành viên"
            subtitle = "Lớp học Toán 10A"
            setDisplayHomeAsUpEnabled(true)
        }

// Xử lý nút back
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}