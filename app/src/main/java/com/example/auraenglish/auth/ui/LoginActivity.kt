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
import com.example.auraenglish.dashboard.ui.DashboardHomeActivity
import com.example.auraenglish.databinding.ActivityAuthLoginBinding
import com.example.auraenglish.practice.utils.setOnClickDebounced
import com.example.auraenglish.practice.utils.showToast

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuthLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyAuthInsets(binding.rootAuthLogin)
        binding.authHeader.bind(this, "A", R.string.auth_login_title, R.string.auth_login_subtitle)
        setupInputs()
        setupActions()
    }

    private fun setupInputs() = with(binding) {
        tilLoginEmail.clearErrorWhenTyping(edtLoginEmail)
        tilLoginPassword.clearErrorWhenTyping(edtLoginPassword)
    }

    private fun setupActions() = with(binding) {
        btnLogin.setOnClickDebounced { submitLogin() }
        btnGoogle.setOnClickDebounced { showToast(getString(R.string.auth_google_demo)) }
        tvForgotPassword.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
        }
        tvOpenRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
    }

    private fun submitLogin() = with(binding) {
        val emailError = AuthFormValidator.validateEmail(edtLoginEmail.inputValue())
        val passwordError = AuthFormValidator.validateRequiredPassword(edtLoginPassword.inputValue())
        tilLoginEmail.showValidationError(emailError)
        tilLoginPassword.showValidationError(passwordError)
        when {
            emailError != null -> edtLoginEmail.requestFocus()
            passwordError != null -> edtLoginPassword.requestFocus()
            else -> {
                startActivity(
                    Intent(this@LoginActivity, DashboardHomeActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
            }
        }
    }
}
