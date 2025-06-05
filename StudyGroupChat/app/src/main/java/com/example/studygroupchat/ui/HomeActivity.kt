package com.example.studygroupchat.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.studygroupchat.databinding.ActivityHomeBinding
import com.example.studygroupchat.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("HomeActivity", "HomeActivity created")

//        setupViews()
//        observeUserData()
    }

    private fun setupViews() {
//        binding.btnLogout.setOnClickListener {
//            viewModel.logout()
//            // Navigate back to LoginActivity
//            val intent = Intent(this, LoginActivity::class.java).apply {
//                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            }
//            startActivity(intent)
//            finish()
//        }
    }

//    private fun observeUserData() {
//        lifecycleScope.launch {
//            viewModel.getUserData().collect { userData ->
//                binding.tvWelcome.text = "Welcome, ${userData.userName}!"
//                binding.tvEmail.text = "Email: ${userData.email}"
//                binding.tvPhone.text = "Phone: ${userData.phoneNumber}"
//            }
//        }
//    }
} 