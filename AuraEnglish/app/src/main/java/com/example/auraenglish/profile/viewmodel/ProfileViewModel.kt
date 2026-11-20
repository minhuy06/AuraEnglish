package com.example.auraenglish.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.auraenglish.profile.data.LearningGoalConfigUiModel
import com.example.auraenglish.profile.data.UserProfileUiModel

object ProfileSessionStore {
    var currentProfile: UserProfileUiModel = UserProfileUiModel()
    var currentGoalConfig: LearningGoalConfigUiModel = LearningGoalConfigUiModel()
}

class ProfileViewModel : ViewModel() {

    private val _userProfile = MutableLiveData(ProfileSessionStore.currentProfile)
    val userProfile: LiveData<UserProfileUiModel> = _userProfile

    private val _goalConfig = MutableLiveData(ProfileSessionStore.currentGoalConfig)
    val goalConfig: LiveData<LearningGoalConfigUiModel> = _goalConfig

    fun refreshFromStore() {
        _userProfile.value = ProfileSessionStore.currentProfile
        _goalConfig.value = ProfileSessionStore.currentGoalConfig
    }

    fun updateProfile(
        fullName: String,
        email: String,
        phone: String,
        learningGoal: String,
        avatarUri: String?
    ) {
        val cleanName = fullName.trim()
        val initials = cleanName
            .split(" ")
            .filter { it.isNotBlank() }
            .takeLast(2)
            .joinToString("") { it.first().uppercase() }
            .ifBlank { "DH" }

        val updated = (_userProfile.value ?: UserProfileUiModel()).copy(
            fullName = cleanName,
            initials = initials,
            email = email.trim(),
            phone = phone.replace(" ", "").trim(),
            learningGoal = learningGoal.trim(),
            avatarUriString = avatarUri ?: _userProfile.value?.avatarUriString
        )
        ProfileSessionStore.currentProfile = updated
        _userProfile.value = updated
    }

    fun setDailyLessons(lessons: Int) {
        val updated = (_goalConfig.value ?: LearningGoalConfigUiModel()).copy(dailyLessons = lessons)
        _goalConfig.value = updated
    }

    fun setReminderTime(timeText: String) {
        val updated = (_goalConfig.value ?: LearningGoalConfigUiModel()).copy(reminderTime = timeText)
        _goalConfig.value = updated
    }

    fun setStreakProtection(enabled: Boolean) {
        val updated = (_goalConfig.value ?: LearningGoalConfigUiModel()).copy(streakProtectionEnabled = enabled)
        _goalConfig.value = updated
    }

    fun setAutoPronounce(enabled: Boolean) {
        val updated = (_goalConfig.value ?: LearningGoalConfigUiModel()).copy(autoPronounceFlashcard = enabled)
        ProfileSessionStore.currentGoalConfig = updated
        _goalConfig.value = updated
    }

    fun commitGoalConfig() {
        _goalConfig.value?.let {
            ProfileSessionStore.currentGoalConfig = it
        }
    }
}
