package com.example.auraenglish.auth.validator

private val EMAIL_PATTERN = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
private val STRONG_PASSWORD_PATTERN = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")

enum class AuthValidationError {
    NAME_REQUIRED,
    EMAIL_REQUIRED,
    EMAIL_INVALID,
    PASSWORD_REQUIRED,
    PASSWORD_WEAK,
    PASSWORD_MISMATCH,
    TERMS_REQUIRED,
    CODE_INCOMPLETE
}

object AuthFormValidator {
    fun validateName(value: String): AuthValidationError? =
        AuthValidationError.NAME_REQUIRED.takeIf { value.trim().isBlank() }

    fun validateEmail(value: String): AuthValidationError? {
        val email = value.trim()
        return when {
            email.isBlank() -> AuthValidationError.EMAIL_REQUIRED
            !EMAIL_PATTERN.matches(email) -> AuthValidationError.EMAIL_INVALID
            else -> null
        }
    }

    fun validateRequiredPassword(value: String): AuthValidationError? =
        AuthValidationError.PASSWORD_REQUIRED.takeIf { value.isBlank() }

    fun validateStrongPassword(value: String): AuthValidationError? = when {
        value.isBlank() -> AuthValidationError.PASSWORD_REQUIRED
        !STRONG_PASSWORD_PATTERN.matches(value) -> AuthValidationError.PASSWORD_WEAK
        else -> null
    }

    fun validateConfirmation(password: String, confirmation: String): AuthValidationError? = when {
        confirmation.isBlank() -> AuthValidationError.PASSWORD_REQUIRED
        password != confirmation -> AuthValidationError.PASSWORD_MISMATCH
        else -> null
    }

    fun validateTerms(accepted: Boolean): AuthValidationError? =
        AuthValidationError.TERMS_REQUIRED.takeUnless { accepted }

    fun validateCode(code: String): AuthValidationError? =
        AuthValidationError.CODE_INCOMPLETE.takeUnless {
            code.length == 6 && code.all(Char::isDigit)
        }
}
