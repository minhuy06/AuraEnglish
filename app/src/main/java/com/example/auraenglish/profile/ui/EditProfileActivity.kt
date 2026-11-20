package com.example.auraenglish.profile.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ActivityEditProfileBinding
import com.example.auraenglish.practice.utils.gone
import com.example.auraenglish.practice.utils.setOnClickDebounced
import com.example.auraenglish.practice.utils.showToast
import com.example.auraenglish.practice.utils.visible
import com.example.auraenglish.profile.data.UserProfileUiModel
import com.example.auraenglish.profile.validator.ProfileFormValidator
import com.example.auraenglish.profile.viewmodel.ProfileViewModel

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    private var selectedAvatarUri: String? = null
    private var hasSeededForm: Boolean = false

    private val pickAvatarLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            selectedAvatarUri = it.toString()
            binding.imgEditAvatarPhoto.setImageURI(it)
            binding.imgEditAvatarPhoto.visible()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedAvatarUri = savedInstanceState?.getString(KEY_AVATAR_URI)
        hasSeededForm = savedInstanceState != null

        setupViews()
        observeViewModel()
        setupRealtimeErrorClearing()
        setupListeners()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_AVATAR_URI, selectedAvatarUri)
    }

    private fun setupViews() = with(binding) {
        ViewCompat.setOnApplyWindowInsetsListener(rootEditProfile) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        val goals = listOf(
            "Công việc / TOEIC",
            "Giao tiếp hàng ngày",
            "Du học / IELTS",
            "Tiếng Anh chuyên ngành IT"
        )
        val adapter = ArrayAdapter(
            this@EditProfileActivity,
            android.R.layout.simple_dropdown_item_1line,
            goals
        )
        actvLearningGoal.setAdapter(adapter)
    }

    private fun observeViewModel() {
        viewModel.userProfile.observe(this) { profile ->
            bindProfile(profile)
        }
    }

    private fun bindProfile(profile: UserProfileUiModel) = with(binding) {
        tvEditAvatarInitials.text = profile.initials
        tvEditCardName.text = profile.fullName

        if (!hasSeededForm) {
            edtFullName.setText(profile.fullName)
            edtEmail.setText(profile.email)
            edtPhone.setText(profile.phone)
            actvLearningGoal.setText(profile.learningGoal, false)
            hasSeededForm = true
        }

        val uriStr = selectedAvatarUri ?: profile.avatarUriString
        if (!uriStr.isNullOrBlank()) {
            imgEditAvatarPhoto.setImageURI(uriStr.toUri())
            imgEditAvatarPhoto.visible()
        } else {
            imgEditAvatarPhoto.gone()
        }
    }

    private fun setupRealtimeErrorClearing() = with(binding) {
        edtFullName.doAfterTextChanged { tilFullName.error = null }
        edtEmail.doAfterTextChanged { tilEmail.error = null }
        edtPhone.doAfterTextChanged { tilPhone.error = null }
    }

    private fun setupListeners() = with(binding) {
        btnBackEditProfile.setOnClickListener { finish() }
        tvCancelEditProfile.setOnClickListener { finish() }

        btnChangeAvatar.setOnClickDebounced {
            pickAvatarLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        btnSaveProfile.setOnClickDebounced {
            if (validateFormCumulatively()) {
                viewModel.updateProfile(
                    fullName = edtFullName.text?.toString().orEmpty(),
                    email = edtEmail.text?.toString().orEmpty(),
                    phone = edtPhone.text?.toString().orEmpty(),
                    learningGoal = actvLearningGoal.text?.toString().orEmpty(),
                    avatarUri = selectedAvatarUri
                )
                showToast(R.string.profile_toast_saved)
                finish()
            }
        }
    }

    private fun validateFormCumulatively(): Boolean = with(binding) {
        var isFormValid = true
        var firstErrorView: View? = null

        val nameRes = ProfileFormValidator.validateFullName(edtFullName.text?.toString())
        if (!nameRes.isValid) {
            tilFullName.error = nameRes.errorMessageRes?.let(::getString)
            isFormValid = false
            if (firstErrorView == null) firstErrorView = edtFullName
        }

        val emailRes = ProfileFormValidator.validateEmail(edtEmail.text?.toString())
        if (!emailRes.isValid) {
            tilEmail.error = emailRes.errorMessageRes?.let(::getString)
            isFormValid = false
            if (firstErrorView == null) firstErrorView = edtEmail
        }

        val phoneRes = ProfileFormValidator.validateVietnamPhone(edtPhone.text?.toString())
        if (!phoneRes.isValid) {
            tilPhone.error = phoneRes.errorMessageRes?.let(::getString)
            isFormValid = false
            if (firstErrorView == null) firstErrorView = edtPhone
        }

        firstErrorView?.requestFocus()
        return isFormValid
    }

    companion object {
        private const val KEY_AVATAR_URI = "key_selected_avatar_uri"
    }
}
