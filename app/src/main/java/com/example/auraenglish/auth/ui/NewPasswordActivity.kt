package com.example.auraenglish.auth.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.auraenglish.R
import com.example.auraenglish.auth.utils.applyAuthInsets
import com.example.auraenglish.auth.utils.bind
import com.example.auraenglish.auth.utils.clearErrorWhenTyping
import com.example.auraenglish.auth.utils.inputValue
import com.example.auraenglish.auth.utils.showValidationError
import com.example.auraenglish.auth.validator.AuthFormValidator
import com.example.auraenglish.databinding.ActivityAuthNewPasswordBinding
import com.example.auraenglish.practice.utils.setOnClickDebounced

class NewPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthNewPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuthNewPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyAuthInsets(binding.rootAuthNewPassword)
        binding.authHeader.bind(
            this,
            "K",
            R.string.auth_new_password_title,
            R.string.auth_new_password_subtitle
        )
        binding.tilNewPassword.clearErrorWhenTyping(binding.edtNewPassword)
        binding.tilNewPasswordConfirmation.clearErrorWhenTyping(binding.edtNewPasswordConfirmation)
        binding.btnSetNewPassword.setOnClickDebounced { submitPassword() }
    }

    private fun submitPassword() = with(binding) {
        val password = edtNewPassword.inputValue()
        val passwordError = AuthFormValidator.validateStrongPassword(password)
        val confirmationError = AuthFormValidator.validateConfirmation(
            password,
            edtNewPasswordConfirmation.inputValue()
        )
        tilNewPassword.showValidationError(passwordError)
        tilNewPasswordConfirmation.showValidationError(confirmationError)
        when {
            passwordError != null -> edtNewPassword.requestFocus()
            confirmationError != null -> edtNewPasswordConfirmation.requestFocus()
            else -> startActivity(Intent(this@NewPasswordActivity, ResetSuccessActivity::class.java))
        }
    }
}
