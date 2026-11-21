package com.example.auraenglish.auth.utils

import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.example.auraenglish.R
import com.example.auraenglish.auth.validator.AuthValidationError
import com.example.auraenglish.databinding.IncludeAuthHeaderBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

fun AppCompatActivity.applyAuthInsets(root: View, includeIme: Boolean = false) {
    ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
        val mask = if (includeIme) {
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
        } else {
            WindowInsetsCompat.Type.systemBars()
        }
        val bars = insets.getInsets(mask)
        view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
        insets
    }
}

fun IncludeAuthHeaderBinding.bind(
    activity: AppCompatActivity,
    logo: String,
    @StringRes titleRes: Int,
    @StringRes subtitleRes: Int
) {
    tvAuthLogo.text = logo
    tvAuthHeaderTitle.setText(titleRes)
    tvAuthHeaderSubtitle.setText(subtitleRes)
    btnAuthBack.setOnClickListener { activity.finish() }
}

fun TextInputEditText.inputValue(): String = text?.toString().orEmpty()

fun TextInputLayout.clearErrorWhenTyping(input: TextInputEditText) {
    input.doAfterTextChanged { error = null }
}

fun TextInputLayout.showValidationError(errorValue: AuthValidationError?) {
    error = errorValue?.let { context.getString(it.messageRes) }
}

val AuthValidationError.messageRes: Int
    get() = when (this) {
        AuthValidationError.NAME_REQUIRED -> R.string.auth_error_name_required
        AuthValidationError.EMAIL_REQUIRED -> R.string.auth_error_email_required
        AuthValidationError.EMAIL_INVALID -> R.string.auth_error_email_invalid
        AuthValidationError.PASSWORD_REQUIRED -> R.string.auth_error_password_required
        AuthValidationError.PASSWORD_WEAK -> R.string.auth_error_password_weak
        AuthValidationError.PASSWORD_MISMATCH -> R.string.auth_error_password_mismatch
        AuthValidationError.TERMS_REQUIRED -> R.string.auth_error_terms_required
        AuthValidationError.CODE_INCOMPLETE -> R.string.auth_error_code_incomplete
    }
