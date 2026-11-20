package com.example.auraenglish.profile.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.auraenglish.R
import com.example.auraenglish.dashboard.ui.DashboardCategoryActivity
import com.example.auraenglish.dashboard.ui.DashboardHomeActivity
import com.example.auraenglish.dashboard.ui.DashboardStoreActivity
import com.example.auraenglish.databinding.ActivityProfileSettingsBinding
import com.example.auraenglish.practice.helper.PracticeNavHelper
import com.example.auraenglish.practice.helper.PracticeTab
import com.example.auraenglish.practice.utils.gone
import com.example.auraenglish.practice.utils.setOnClickDebounced
import com.example.auraenglish.practice.utils.showToast
import com.example.auraenglish.practice.utils.visible
import com.example.auraenglish.profile.data.LearningGoalConfigUiModel
import com.example.auraenglish.profile.data.UserProfileUiModel
import com.example.auraenglish.profile.helper.ProfileLogoutDialogHelper
import com.example.auraenglish.profile.viewmodel.ProfileViewModel

class ProfileSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSettingsBinding
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeViewModel()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshFromStore()
    }

    private fun setupViews() = with(binding) {
        ViewCompat.setOnApplyWindowInsetsListener(rootProfileSettings) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        PracticeNavHelper.bind(this@ProfileSettingsActivity, includeBottomNav, PracticeTab.SETTINGS)
    }

    private fun observeViewModel() {
        viewModel.userProfile.observe(this) { profile ->
            bindProfileData(profile)
        }
        viewModel.goalConfig.observe(this) { config ->
            bindGoalConfig(config)
        }
    }

    private fun bindProfileData(profile: UserProfileUiModel) = with(binding) {
        tvProfileName.text = profile.fullName
        tvAvatarInitials.text = profile.initials
        tvProfileLevelBadge.text = profile.levelBadge
        tvProfileEmail.text = profile.email
        tvProfileStreakXp.text = profile.streakXpSummary

        val uriStr = profile.avatarUriString
        if (!uriStr.isNullOrBlank()) {
            imgAvatarPhoto.setImageURI(uriStr.toUri())
            imgAvatarPhoto.visible()
        } else {
            imgAvatarPhoto.gone()
        }
    }

    private fun bindGoalConfig(config: LearningGoalConfigUiModel) = with(binding) {
        tvGoalRowSub.text = config.toSummarySubtitle()
        if (swAutoPronounce.isChecked != config.autoPronounceFlashcard) {
            swAutoPronounce.isChecked = config.autoPronounceFlashcard
        }
    }

    private fun setupListeners() = with(binding) {
        val openEditProfile: () -> Unit = {
            startActivity(Intent(this@ProfileSettingsActivity, EditProfileActivity::class.java))
        }

        cardProfileSummary.setOnClickDebounced { openEditProfile() }
        rowPersonalInfo.setOnClickDebounced { openEditProfile() }

        rowGoalReminder.setOnClickDebounced {
            startActivity(Intent(this@ProfileSettingsActivity, GoalReminderActivity::class.java))
        }

        rowChangePassword.setOnClickDebounced {
            startActivity(Intent(this@ProfileSettingsActivity, ChangePasswordActivity::class.java))
        }

        rowCurrentRoute.setOnClickDebounced {
            startActivity(Intent(this@ProfileSettingsActivity, DashboardCategoryActivity::class.java))
        }

        rowPurchasedExams.setOnClickDebounced {
            startActivity(Intent(this@ProfileSettingsActivity, DashboardStoreActivity::class.java))
        }

        btnSettingsCart.setOnClickDebounced {
            startActivity(Intent(this@ProfileSettingsActivity, DashboardStoreActivity::class.java))
        }

        btnSettingsBell.setOnClickDebounced {
            startActivity(Intent(this@ProfileSettingsActivity, GoalReminderActivity::class.java))
        }

        swAutoPronounce.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setAutoPronounce(isChecked)
        }

        cardLogout.setOnClickDebounced {
            ProfileLogoutDialogHelper.showLogoutConfirmDialog(this@ProfileSettingsActivity) {
                showToast(R.string.profile_toast_logged_out)
                startActivity(
                    Intent(this@ProfileSettingsActivity, DashboardHomeActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                finish()
            }
        }
    }
}
