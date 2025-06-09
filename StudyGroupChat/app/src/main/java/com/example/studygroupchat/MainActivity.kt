package com.example.studygroupchat

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.studygroupchat.ui.HomeActivity
import com.example.studygroupchat.ui.LoginActivity
import com.example.studygroupchat.ui.fragments.CreateRoomFragment
import com.example.studygroupchat.ui.fragments.HomeFragment
import com.example.studygroupchat.ui.fragments.JoinRoomFragment
import com.example.studygroupchat.ui.fragments.ProfileFragment
import com.example.studygroupchat.viewmodel.AuthViewModel
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {
    private val viewModel: AuthViewModel by viewModels()

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var fab: FloatingActionButton
    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val userData = viewModel.getTokenData().first()
            if (userData.accessToken.isEmpty()) {
                // Nếu chưa đăng nhập => sang LoginActivity
                startActivity(Intent(this@MainActivity, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                finish()
            } else {
                // Đã đăng nhập => hiển thị giao diện chính
                setContentView(R.layout.activity_main)
                setupMainUI()

                viewModel.startAutoRefreshToken()

                // Ánh xạ view sau setContentView
                fab = findViewById(R.id.floatingActionButton)
                bottomAppBar = findViewById(R.id.bottomAppBar)
                bottomNav = findViewById(R.id.bottom_nav)

                // Lắng nghe back stack để hiện lại bottom khi quay về
                supportFragmentManager.addOnBackStackChangedListener {
                    val isCreateRoomFragmentVisible = supportFragmentManager.fragments.any { it is CreateRoomFragment }
                    if (!isCreateRoomFragmentVisible) {
                        fab.visibility = View.VISIBLE
                        bottomAppBar.visibility = View.VISIBLE
                        bottomNav.visibility = View.VISIBLE
                    }
                }

                fab.setOnClickListener {
                    fab.visibility = View.GONE
                    bottomAppBar.visibility = View.GONE
                    bottomNav.visibility = View.GONE

                    val createRoomFragment = CreateRoomFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, createRoomFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }




    }

    private fun setupMainUI() {
        bottomNavigation = findViewById(R.id.bottom_nav)

        // Mặc định hiển thị HomeFragment
        loadFragment(HomeFragment())

        // Xử lý chọn bottom nav
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> loadFragment(HomeFragment())
                R.id.navigation_classroom -> loadFragment(JoinRoomFragment())
                R.id.navigation_profile -> loadFragment(ProfileFragment()) }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
