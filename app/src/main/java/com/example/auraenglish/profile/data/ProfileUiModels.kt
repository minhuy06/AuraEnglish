package com.example.auraenglish.profile.data

data class UserProfileUiModel(
    val fullName: String = "Duy Hưng",
    val initials: String = "DH",
    val levelBadge: String = "B1 • Trung cấp",
    val email: String = "duyhung@example.com",
    val phone: String = "0912345678",
    val learningGoal: String = "Công việc / TOEIC",
    val streakXpSummary: String = "Chuỗi 7 ngày • 1.920 XP",
    val avatarUriString: String? = null
)

data class LearningGoalConfigUiModel(
    val dailyLessons: Int = 5,
    val reminderTime: String = "20:00",
    val streakProtectionEnabled: Boolean = true,
    val autoPronounceFlashcard: Boolean = true
) {
    fun toSummarySubtitle(): String = "$dailyLessons bài/ngày • Nhắc lúc $reminderTime"
}
