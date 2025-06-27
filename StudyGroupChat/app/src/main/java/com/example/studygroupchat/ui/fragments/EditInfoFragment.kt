package com.example.studygroupchat.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.studygroupchat.R
import com.example.studygroupchat.api.ApiConfig
import com.example.studygroupchat.repository.UserRepository
import com.example.studygroupchat.StudyGroupChatApplication
import com.example.studygroupchat.viewmodel.UserViewModel
import com.example.studygroupchat.viewmodel.UserViewModelFactory
import com.google.android.material.imageview.ShapeableImageView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class EditInfoFragment : Fragment() {

    private val viewModel: UserViewModel by viewModels({ requireParentFragment() }) {
        val app = requireActivity().application as StudyGroupChatApplication
        UserViewModelFactory(
            UserRepository(
                ApiConfig.userApiService,
                app.database.userDao()
            )
        )
    }

    private var selectedPart: MultipartBody.Part? = null

    private lateinit var editFullName: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPhone: EditText
    private lateinit var imgAvatar: ShapeableImageView
    private lateinit var tvChangePhoto: TextView
    private lateinit var editUserName: TextView

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { handleImageSelected(it) }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_edit_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editFullName = view.findViewById(R.id.editFullName)
        editUserName = view.findViewById(R.id.editUserName)
        editEmail = view.findViewById(R.id.editEmail)
        editPhone = view.findViewById(R.id.editPhone)
        imgAvatar = view.findViewById(R.id.imgAvatar)
        tvChangePhoto = view.findViewById(R.id.tvChangePhoto)

        viewModel.user.observe(viewLifecycleOwner) { user ->
            editFullName.setText(user.fullName)
            editEmail.setText(user.email)
            editUserName.setText(user.userName)
            editPhone.setText(user.phoneNumber)
            user.avatarUrl?.let {
                Glide.with(this).load(it)
                    .placeholder(R.drawable.baseline_account_circle_24)
                    .into(imgAvatar)
            }
        }

        imgAvatar.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        tvChangePhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        viewModel.fetchCurrentUser()
    }


    private fun handleImageSelected(uri: Uri) {
        imgAvatar.setImageURI(uri)
        val file = File(requireContext().cacheDir, "avatar")
        val input = requireContext().contentResolver.openInputStream(uri)
        file.outputStream().use { out ->
            input?.copyTo(out)
        }
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        selectedPart = MultipartBody.Part.createFormData("avatar", file.name, requestBody)
    }

    fun saveChanges() {
        viewModel.updateCurrentUser(
            selectedPart,
            editFullName.text.toString(),
            editEmail.text.toString(),
            editPhone.text.toString()
        )
    }
}