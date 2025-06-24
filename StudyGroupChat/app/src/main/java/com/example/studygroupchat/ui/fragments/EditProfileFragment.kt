package com.example.studygroupchat.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager2.widget.ViewPager2
import com.example.studygroupchat.R
import com.example.studygroupchat.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.example.studygroupchat.api.ApiConfig
import com.example.studygroupchat.repository.UserRepository
import com.example.studygroupchat.StudyGroupChatApplication
import com.example.studygroupchat.viewmodel.UserViewModel
import com.example.studygroupchat.viewmodel.UserViewModelFactory
import androidx.fragment.app.viewModels
import com.example.studygroupchat.MainActivity
import com.example.studygroupchat.ui.fragments.ChangePasswordFragment


class EditProfileFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: ViewPagerAdapter
    private val userViewModel: UserViewModel by viewModels {
        val app = requireActivity().application as StudyGroupChatApplication
        UserViewModelFactory(
            UserRepository(
                ApiConfig.userApiService,
                app.database.userDao()
            )
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)

        adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        // Kết nối tabLayout và viewPager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Thông tin cá nhân"
                1 -> "Mật khẩu"
                else -> ""
            }
        }.attach()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnCancel = view.findViewById<ImageView>(R.id.btnCancel)
        val btnSave = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSave)

        btnCancel.setOnClickListener {
            // TODO: Xử lý đăng xuất hoặc hủy chỉnh sửa
            handleCancel()
        }

        btnSave.setOnClickListener {
            val tag = "f" + viewPager.currentItem
            val fragment = childFragmentManager.findFragmentByTag(tag)
            if (fragment is EditInfoFragment) {
                fragment.saveChanges()

            } else if (fragment is ChangePasswordFragment) {
                fragment.changePassword()
            }
        }
        userViewModel.updateResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                android.widget.Toast.makeText(requireContext(), "\u0110\u00E3 c\u1EADp nh\u1EADt h\u1ED3 s\u01A1", android.widget.Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
            result.onFailure {
                android.widget.Toast.makeText(requireContext(), "C\u1EADp nh\u1EADt th\u1EA5t b\u1EA1i", android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        userViewModel.passwordResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                android.widget.Toast.makeText(requireContext(), "\u0110\u00E3 c\u1EADp nh\u1EADt m\u1EADt kh\u1EA9u", android.widget.Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
            result.onFailure {
                android.widget.Toast.makeText(requireContext(), "C\u1EADp nh\u1EADt th\u1EA5t b\u1EA1i", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun handleCancel() {
        //quay về màn hình trước đó trong Navigation component
        parentFragmentManager.popBackStack()


    }


}