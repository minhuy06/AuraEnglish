package com.example.auraenglish.practice.ui

import com.example.auraenglish.practice.helper.*

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ActivityPracticeDemoTabBinding

// Demo tab LIBRARY / SETTINGS (và fallback)
class PracticeDemoTabActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPracticeDemoTabBinding
    private lateinit var tab: PracticeTab

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPracticeDemoTabBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        tab = runCatching {
            PracticeTab.valueOf(intent.getStringExtra(PracticeNavHelper.EXTRA_TAB) ?: PracticeTab.HOME.name)
        }.getOrDefault(PracticeTab.HOME)

        val (title, desc, icon) = when (tab) {
            PracticeTab.HOME -> Triple(
                getString(R.string.practice_nav_home),
                getString(R.string.practice_demo_home_desc),
                R.drawable.ic_nav_home
            )
            PracticeTab.LIBRARY -> Triple(
                getString(R.string.practice_nav_library),
                getString(R.string.practice_demo_library_desc),
                R.drawable.ic_nav_library
            )
            // RANK chính đi RankingActivity; nhánh này chỉ fallback
            PracticeTab.RANK -> Triple(
                getString(R.string.practice_nav_rank),
                getString(R.string.practice_demo_rank_desc),
                R.drawable.ic_nav_rank
            )
            PracticeTab.SETTINGS -> Triple(
                getString(R.string.practice_nav_settings),
                getString(R.string.practice_demo_settings_desc),
                R.drawable.ic_nav_settings
            )
            PracticeTab.PRACTICE -> Triple(
                getString(R.string.practice_nav_practice),
                "",
                R.drawable.ic_nav_practice
            )
        }

        binding.tvTitle.text = title
        binding.tvHeadline.text = title
        binding.tvDescription.text = desc
        binding.ivIcon.setImageResource(icon)
        binding.ivIcon.setColorFilter(getColor(R.color.practice_primary))

        // Thư viện để trống — không hiện danh sách từ
        binding.librarySection.isVisible = false

        binding.btnBack.setOnClickListener { goHome() }
        binding.btnPrimary.setOnClickListener { goHome() }
        PracticeNavHelper.bind(this, binding.bottomNavInclude, tab)
    }

    private fun goHome() {
        startActivity(
            Intent(this, com.example.auraenglish.dashboard.ui.DashboardHomeActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
        finish()
    }
}
