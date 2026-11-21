package com.example.auraenglish.auth.validator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthFormValidatorTest {

    @Test
    fun email_rejectsBlankAndMalformedValues() {
        assertEquals(AuthValidationError.EMAIL_REQUIRED, AuthFormValidator.validateEmail("   "))
        assertEquals(AuthValidationError.EMAIL_INVALID, AuthFormValidator.validateEmail("user@invalid"))
        assertNull(AuthFormValidator.validateEmail("  user@example.com  "))
    }

    @Test
    fun strongPassword_requiresLengthUpperLowerAndNumber() {
        assertEquals(AuthValidationError.PASSWORD_REQUIRED, AuthFormValidator.validateStrongPassword(""))
        assertEquals(AuthValidationError.PASSWORD_WEAK, AuthFormValidator.validateStrongPassword("password"))
        assertEquals(AuthValidationError.PASSWORD_WEAK, AuthFormValidator.validateStrongPassword("Password"))
        assertNull(AuthFormValidator.validateStrongPassword("Password1"))
    }

    @Test
    fun confirmation_mustMatchPassword() {
        assertEquals(
            AuthValidationError.PASSWORD_MISMATCH,
            AuthFormValidator.validateConfirmation("Password1", "Password2")
        )
        assertNull(AuthFormValidator.validateConfirmation("Password1", "Password1"))
    }

    @Test
    fun terms_mustBeAccepted() {
        assertEquals(AuthValidationError.TERMS_REQUIRED, AuthFormValidator.validateTerms(false))
        assertNull(AuthFormValidator.validateTerms(true))
    }

    @Test
    fun code_requiresExactlySixDigits() {
        assertEquals(AuthValidationError.CODE_INCOMPLETE, AuthFormValidator.validateCode("12345"))
        assertEquals(AuthValidationError.CODE_INCOMPLETE, AuthFormValidator.validateCode("12345A"))
        assertNull(AuthFormValidator.validateCode("814206"))
    }
}
