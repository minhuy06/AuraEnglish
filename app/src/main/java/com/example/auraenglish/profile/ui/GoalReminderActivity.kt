package com.example.auraenglish.profile.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ActivityGoalReminderBinding
import com.example.auraenglish.practice.utils.setOnClickDebounced
import com.example.auraenglish.practice.utils.showToast
import com.example.auraenglish.practice.utils.visibleIf
import com.example.auraenglish.profile.data.LearningGoalConfigUiModel
import com.example.auraenglish.profile.viewmodel.ProfileViewModel
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.Locale

class GoalReminderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalReminderBinding
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGoalReminderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeViewModel()
        setupListeners()
    }

    private fun setupViews() = with(binding) {
        ViewCompat.setOnApplyWindowInsetsListener(rootGoalReminder) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }

    private fun observeViewModel() {
        viewModel.goalConfig.observe(this) { config ->
            bindGoalState(config)
        }
    }

    private fun bindGoalState(config: LearningGoalConfigUiModel) = with(binding) {
        val is3 = config.dailyLessons == 3
        val is5 = config.dailyLessons == 5
        val is10 = config.dailyLessons == 10

        cardGoal3.setBackgroundResource(
            if (is3) R.drawable.bg_profile_card_selected else R.drawable.bg_profile_card
        )
        cardGoal5.setBackgroundResource(
            if (is5) R.drawable.bg_profile_card_selected else R.drawable.bg_profile_card
        )
        cardGoal10.setBackgroundResource(
            if (is10) R.drawable.bg_profile_card_selected else R.drawable.bg_profile_card
        )

        tvGoal3Check.visibleIf(is3)
        tvGoal5Check.visibleIf(is5)
        tvGoal10Check.visibleIf(is10)

        tvGoal3Sub.text = if (is3) "Khoảng 10 phút mỗi ngày • Đang chọn" else getString(R.string.profile_goal_3_sub)
        tvGoal5Sub.text = if (is5) getString(R.string.profile_goal_5_sub_selected) else getString(R.string.profile_goal_5_sub)
        tvGoal10Sub.text = if (is10) "Khoảng 30 phút mỗi ngày • Đang chọn" else getString(R.string.profile_goal_10_sub)

        val activeColor = ContextCompat.getColor(this@GoalReminderActivity, R.color.profile_primary_royal)
        val normalColor = ContextCompat.getColor(this@GoalReminderActivity, R.color.profile_text_secondary)

        tvGoal3Sub.setTextColor(if (is3) activeColor else normalColor)
        tvGoal5Sub.setTextColor(if (is5) activeColor else normalColor)
        tvGoal10Sub.setTextColor(if (is10) activeColor else normalColor)

        tvReminderTimeChip.text = config.reminderTime
        if (swStreakProtection.isChecked != config.streakProtectionEnabled) {
            swStreakProtection.isChecked = config.streakProtectionEnabled
        }
    }

    private fun setupListeners() = with(binding) {
        btnBackGoal.setOnClickListener { finish() }
        tvBackFromGoal.setOnClickListener { finish() }

        cardGoal3.setOnClickListener { viewModel.setDailyLessons(3) }
        cardGoal5.setOnClickListener { viewModel.setDailyLessons(5) }
        cardGoal10.setOnClickListener { viewModel.setDailyLessons(10) }

        rowReminderTime.setOnClickDebounced {
            showTimePicker()
        }

        swStreakProtection.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setStreakProtection(isChecked)
        }

        btnSaveGoalSettings.setOnClickDebounced {
            viewModel.commitGoalConfig()
            showToast(R.string.profile_toast_goal_saved)
            finish()
        }
    }

    private fun showTimePicker() {
        val current = viewModel.goalConfig.value?.reminderTime ?: "20:00"
        val parts = current.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 20
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(hour)
            .setMinute(minute)
            .setTitleText(getString(R.string.profile_reminder_time_title))
            .build()

        picker.addOnPositiveButtonClickListener {
            val formatted = String.format(Locale.US, "%02d:%02d", picker.hour, picker.minute)
            viewModel.setReminderTime(formatted)
        }
        picker.show(supportFragmentManager, "GOAL_REMINDER_TIME_PICKER")
    }
}
