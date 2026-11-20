package com.example.auraenglish.auth.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.auraenglish.R
import com.example.auraenglish.auth.AuthContract
import com.example.auraenglish.auth.utils.applyAuthInsets
import com.example.auraenglish.auth.utils.bind
import com.example.auraenglish.auth.utils.clearErrorWhenTyping
import com.example.auraenglish.auth.utils.inputValue
import com.example.auraenglish.auth.utils.showValidationError
import com.example.auraenglish.auth.validator.AuthFormValidator
import com.example.auraenglish.databinding.ActivityAuthForgotPasswordBinding
import com.example.auraenglish.practice.utils.setOnClickDebounced

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuthForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyAuthInsets(binding.rootAuthForgotPassword)
        binding.authHeader.bind(this, "L", R.string.auth_forgot_title, R.string.auth_forgot_subtitle)
        binding.tilForgotEmail.clearErrorWhenTyping(binding.edtForgotEmail)
        binding.btnSendResetCode.setOnClickDebounced { submitEmail() }
        binding.tvBackToLogin.setOnClickListener { finish() }
    }

    private fun submitEmail() = with(binding) {
        val error = AuthFormValidator.validateEmail(edtForgotEmail.inputValue())
        tilForgotEmail.showValidationError(error)
        if (error != null) {
            edtForgotEmail.requestFocus()
            return@with
        }
        startActivity(
            Intent(this@ForgotPasswordActivity, ResetCodeActivity::class.java)
                .putExtra(AuthContract.EXTRA_EMAIL, edtForgotEmail.inputValue().trim())
        )
    }
}
