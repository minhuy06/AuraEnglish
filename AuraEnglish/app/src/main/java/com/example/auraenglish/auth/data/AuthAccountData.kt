package com.example.auraenglish.auth.data

enum class UserRole {
    USER,
    ADMIN
}

data class AuthUser(
    val email: String,
    val password: String,
    val fullName: String,
    val role: UserRole
)

object AuthAccountData {
    val ADMIN_ACCOUNT = AuthUser(
        email = "admin@auraenglish.com",
        password = "Admin@123",
        fullName = "Quản trị viên AuraEnglish",
        role = UserRole.ADMIN
    )

    val DEFAULT_USER_ACCOUNT = AuthUser(
        email = "user@auraenglish.com",
        password = "User@123",
        fullName = "Học viên AuraEnglish",
        role = UserRole.USER
    )

    fun isAdminEmail(email: String): Boolean {
        val trimmed = email.trim()
        return trimmed.equals(ADMIN_ACCOUNT.email, ignoreCase = true) ||
                trimmed.equals("admin", ignoreCase = true) ||
                trimmed.equals("admin@admin.com", ignoreCase = true) ||
                trimmed.equals("admin@aura.edu.vn", ignoreCase = true) ||
                trimmed.equals("admin@aura.com", ignoreCase = true)
    }

    fun isAdminPassword(password: String): Boolean {
        val trimmed = password.trim()
        return trimmed == ADMIN_ACCOUNT.password ||
                trimmed == "admin" ||
                trimmed == "admin123" ||
                trimmed == "Admin123"
    }

    fun isAdmin(email: String, password: String): Boolean {
        return isAdminEmail(email) && isAdminPassword(password)
    }
}
