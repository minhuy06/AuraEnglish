package com.example.auraenglish.auth.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
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
import com.example.auraenglish.databinding.ActivityAuthRegisterBinding
import com.example.auraenglish.practice.utils.setOnClickDebounced

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuthRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyAuthInsets(binding.rootAuthRegister)
        binding.authHeader.bind(this, "+", R.string.auth_register_title, R.string.auth_register_subtitle)
        setupInputs()
        setupActions()
    }

    private fun setupInputs() = with(binding) {
        tilRegisterName.clearErrorWhenTyping(edtRegisterName)
        tilRegisterEmail.clearErrorWhenTyping(edtRegisterEmail)
        tilRegisterPassword.clearErrorWhenTyping(edtRegisterPassword)
        tilRegisterConfirmation.clearErrorWhenTyping(edtRegisterConfirmation)
        cbAcceptTerms.setOnCheckedChangeListener { _, checked ->
            if (checked) tvTermsError.visibility = View.GONE
        }
    }

    private fun setupActions() = with(binding) {
        btnRegister.setOnClickDebounced { submitRegistration() }
        tvRegisterLogin.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            finish()
        }
    }

    private fun submitRegistration() = with(binding) {
        val nameError = AuthFormValidator.validateName(edtRegisterName.inputValue())
        val emailError = AuthFormValidator.validateEmail(edtRegisterEmail.inputValue())
        val password = edtRegisterPassword.inputValue()
        val passwordError = AuthFormValidator.validateStrongPassword(password)
        val confirmationError = AuthFormValidator.validateConfirmation(
            password,
            edtRegisterConfirmation.inputValue()
        )
        val termsError = AuthFormValidator.validateTerms(cbAcceptTerms.isChecked)

        tilRegisterName.showValidationError(nameError)
        tilRegisterEmail.showValidationError(emailError)
        tilRegisterPassword.showValidationError(passwordError)
        tilRegisterConfirmation.showValidationError(confirmationError)
        tvTermsError.visibility = if (termsError == null) View.GONE else View.VISIBLE

        val firstInvalidInput = when {
            nameError != null -> edtRegisterName
            emailError != null -> edtRegisterEmail
            passwordError != null -> edtRegisterPassword
            confirmationError != null -> edtRegisterConfirmation
            else -> null
        }
        if (firstInvalidInput != null || termsError != null) {
            firstInvalidInput?.requestFocus()
            return@with
        }

        startActivity(
            Intent(this@RegisterActivity, VerifyEmailActivity::class.java)
                .putExtra(AuthContract.EXTRA_EMAIL, edtRegisterEmail.inputValue().trim())
        )
    }
}
