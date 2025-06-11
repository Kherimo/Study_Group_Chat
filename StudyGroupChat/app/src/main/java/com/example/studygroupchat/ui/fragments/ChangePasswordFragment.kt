package com.example.studygroupchat.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.studygroupchat.R
import com.example.studygroupchat.api.ApiConfig
import com.example.studygroupchat.repository.UserRepository
import com.example.studygroupchat.viewmodel.UserViewModel
import com.example.studygroupchat.viewmodel.UserViewModelFactory

class ChangePasswordFragment : Fragment() {

    private val viewModel: UserViewModel by viewModels({ requireParentFragment() }) {
        UserViewModelFactory(UserRepository(ApiConfig.userApiService))
    }

    private lateinit var edtCurrent: EditText
    private lateinit var edtNew: EditText
    private lateinit var edtConfirm: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_change_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        edtCurrent = view.findViewById(R.id.edtCurrentPassword)
        edtNew = view.findViewById(R.id.edtNewPassword)
        edtConfirm = view.findViewById(R.id.edtConfirmPassword)
    }

    fun changePassword() {
        val current = edtCurrent.text.toString()
        val newPass = edtNew.text.toString()
        val confirm = edtConfirm.text.toString()
        if (newPass == confirm && current.isNotEmpty() && newPass.isNotEmpty()) {
            viewModel.changePassword(current, newPass)
        }
    }
}