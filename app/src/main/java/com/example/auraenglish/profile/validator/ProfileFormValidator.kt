package com.example.auraenglish.profile.validator

import androidx.annotation.StringRes
import com.example.auraenglish.R

data class ValidationResult(
    val isValid: Boolean,
    @get:StringRes val errorMessageRes: Int? = null
)

object ProfileFormValidator {

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val PHONE_VN_REGEX = Regex("^(03|05|07|08|09)\\d{8}$")
    private val PASSWORD_STRONG_REGEX = Regex("^(?=.*[A-Za-z])(?=.*\\d).{8,}$")

    fun validateFullName(rawName: String?): ValidationResult {
        val name = rawName?.trim().orEmpty()
        if (name.isBlank()) {
            return ValidationResult(false, R.string.profile_err_name_empty)
        }
        if (name.length < 2) {
            return ValidationResult(false, R.string.profile_err_name_short)
        }
        return ValidationResult(true)
    }

    fun validateEmail(rawEmail: String?): ValidationResult {
        val email = rawEmail?.trim().orEmpty()
        if (email.isBlank() || !EMAIL_REGEX.matches(email)) {
            return ValidationResult(false, R.string.profile_err_email_invalid)
        }
        return ValidationResult(true)
    }

    fun validateVietnamPhone(rawPhone: String?): ValidationResult {
        val digitsOnly = rawPhone?.replace(" ", "")?.trim().orEmpty()
        if (!PHONE_VN_REGEX.matches(digitsOnly)) {
            return ValidationResult(false, R.string.profile_err_phone_invalid)
        }
        return ValidationResult(true)
    }

    fun validateCurrentPassword(rawCurrent: String?): ValidationResult {
        val current = rawCurrent?.trim().orEmpty()
        if (current.isBlank()) {
            return ValidationResult(false, R.string.profile_err_pwd_current_empty)
        }
        return ValidationResult(true)
    }

    fun validateNewPassword(rawCurrent: String?, rawNew: String?): ValidationResult {
        val current = rawCurrent?.trim().orEmpty()
        val newPwd = rawNew?.trim().orEmpty()
        if (!PASSWORD_STRONG_REGEX.matches(newPwd)) {
            return ValidationResult(false, R.string.profile_err_pwd_rule)
        }
        if (current.isNotBlank() && current == newPwd) {
            return ValidationResult(false, R.string.profile_err_pwd_same)
        }
        return ValidationResult(true)
    }

    fun validateConfirmPassword(rawNew: String?, rawConfirm: String?): ValidationResult {
        val newPwd = rawNew?.trim().orEmpty()
        val confirm = rawConfirm?.trim().orEmpty()
        if (confirm.isBlank() || confirm != newPwd) {
            return ValidationResult(false, R.string.profile_err_pwd_mismatch)
        }
        return ValidationResult(true)
    }
}
