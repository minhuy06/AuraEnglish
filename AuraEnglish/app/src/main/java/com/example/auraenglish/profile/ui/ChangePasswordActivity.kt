package com.example.auraenglish.profile.ui

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ActivityChangePasswordBinding
import com.example.auraenglish.practice.utils.setOnClickDebounced
import com.example.auraenglish.practice.utils.showToast
import com.example.auraenglish.profile.validator.ProfileFormValidator

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupRealtimeErrorClearing()
        setupListeners()
    }

    private fun setupViews() = with(binding) {
        ViewCompat.setOnApplyWindowInsetsListener(rootChangePassword) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }

    private fun setupRealtimeErrorClearing() = with(binding) {
        edtCurrentPassword.doAfterTextChanged { tilCurrentPassword.error = null }
        edtNewPassword.doAfterTextChanged { tilNewPassword.error = null }
        edtConfirmPassword.doAfterTextChanged { tilConfirmPassword.error = null }
    }

    private fun setupListeners() = with(binding) {
        btnBackChangePassword.setOnClickListener { finish() }
        tvCancelChangePassword.setOnClickListener { finish() }

        tvForgotCurrentPassword.setOnClickDebounced {
            showToast("Hướng dẫn đặt lại mật khẩu đã được gửi tới email của bạn")
        }

        btnUpdatePassword.setOnClickDebounced {
            if (validatePasswordForm()) {
                showToast(R.string.profile_toast_pwd_updated)
                finish()
            }
        }
    }

    private fun validatePasswordForm(): Boolean = with(binding) {
        var isValid = true
        var firstErrorView: View? = null

        val rawCurrent = edtCurrentPassword.text?.toString()
        val rawNew = edtNewPassword.text?.toString()
        val rawConfirm = edtConfirmPassword.text?.toString()

        val currentRes = ProfileFormValidator.validateCurrentPassword(rawCurrent)
        if (!currentRes.isValid) {
            tilCurrentPassword.error = currentRes.errorMessageRes?.let(::getString)
            isValid = false
            if (firstErrorView == null) firstErrorView = edtCurrentPassword
        }

        val newRes = ProfileFormValidator.validateNewPassword(rawCurrent, rawNew)
        if (!newRes.isValid) {
            tilNewPassword.error = newRes.errorMessageRes?.let(::getString)
            isValid = false
            if (firstErrorView == null) firstErrorView = edtNewPassword
        }

        val confirmRes = ProfileFormValidator.validateConfirmPassword(rawNew, rawConfirm)
        if (!confirmRes.isValid) {
            tilConfirmPassword.error = confirmRes.errorMessageRes?.let(::getString)
            isValid = false
            if (firstErrorView == null) firstErrorView = edtConfirmPassword
        }

        firstErrorView?.requestFocus()
        return isValid
    }
}
