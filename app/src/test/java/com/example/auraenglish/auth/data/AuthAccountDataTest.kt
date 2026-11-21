package com.example.auraenglish.auth.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthAccountDataTest {

    @Test
    fun admin_credentials_recognizedCorrectly() {
        // Standard admin credentials
        assertTrue(AuthAccountData.isAdmin("admin@auraenglish.com", "Admin@123"))
        assertTrue(AuthAccountData.isAdmin("ADMIN@AURAENGLISH.COM", "Admin@123"))

        // Convenient short aliases
        assertTrue(AuthAccountData.isAdmin("admin", "admin"))
        assertTrue(AuthAccountData.isAdmin("admin", "admin123"))
        assertTrue(AuthAccountData.isAdmin("admin@admin.com", "Admin@123"))
    }

    @Test
    fun admin_credentials_rejectedWhenInvalid() {
        // Wrong password
        assertFalse(AuthAccountData.isAdmin("admin@auraenglish.com", "wrongpass"))
        assertFalse(AuthAccountData.isAdmin("admin", "wrongpass"))

        // Regular user credentials
        assertFalse(AuthAccountData.isAdmin("user@auraenglish.com", "User@123"))
        assertFalse(AuthAccountData.isAdmin("student@example.com", "12345678"))
    }

    @Test
    fun adminEmail_and_passwordHelpers() {
        assertTrue(AuthAccountData.isAdminEmail("admin@auraenglish.com"))
        assertTrue(AuthAccountData.isAdminEmail("admin"))
        assertFalse(AuthAccountData.isAdminEmail("user@example.com"))

        assertTrue(AuthAccountData.isAdminPassword("Admin@123"))
        assertTrue(AuthAccountData.isAdminPassword("admin"))
        assertFalse(AuthAccountData.isAdminPassword("Password123"))
    }
}
