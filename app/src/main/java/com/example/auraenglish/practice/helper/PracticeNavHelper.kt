package com.example.auraenglish.practice.helper

import android.app.Activity
import android.content.Intent
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.auraenglish.R
import com.example.auraenglish.databinding.IncludePracticeBottomNavBinding
import com.example.auraenglish.dashboard.ui.DashboardCategoryActivity
import com.example.auraenglish.dashboard.ui.DashboardHomeActivity
import com.example.auraenglish.practice.ui.PracticeDemoTabActivity
import com.example.auraenglish.ranking.ui.RankingActivity

// Tab bottom nav (chung practice + ranking)
enum class PracticeTab { HOME, LIBRARY, PRACTICE, RANK, SETTINGS }

// Gắn bottom nav
object PracticeNavHelper {

    // Extra → PracticeDemoTabActivity
    const val EXTRA_TAB = "extra_demo_tab"

    // Highlight + click tab
    fun bind(
        activity: Activity,
        nav: IncludePracticeBottomNavBinding,
        active: PracticeTab
    ) {
        style(nav.ivNavHome, nav.tvNavHome, active == PracticeTab.HOME)
        style(nav.ivNavLibrary, nav.tvNavLibrary, active == PracticeTab.LIBRARY)
        style(nav.ivNavPractice, nav.tvNavPractice, active == PracticeTab.PRACTICE)
        style(nav.ivNavRank, nav.tvNavRank, active == PracticeTab.RANK)
        style(nav.ivNavSettings, nav.tvNavSettings, active == PracticeTab.SETTINGS)

        // Tint icons for inactive/active
        tint(nav.ivNavHome, active == PracticeTab.HOME)
        tint(nav.ivNavLibrary, active == PracticeTab.LIBRARY)
        tint(nav.ivNavPractice, active == PracticeTab.PRACTICE)
        tint(nav.ivNavRank, active == PracticeTab.RANK)
        tint(nav.ivNavSettings, active == PracticeTab.SETTINGS)

        nav.navHome.setOnClickListener { open(activity, PracticeTab.HOME, active) }
        nav.navLibrary.setOnClickListener { open(activity, PracticeTab.LIBRARY, active) }
        nav.navPractice.setOnClickListener { open(activity, PracticeTab.PRACTICE, active) }
        nav.navRank.setOnClickListener { open(activity, PracticeTab.RANK, active) }
        nav.navSettings.setOnClickListener { open(activity, PracticeTab.SETTINGS, active) }
    }

    // Điều hướng tab.
    private fun open(activity: Activity, target: PracticeTab, current: PracticeTab) {
        if (target == current) return
        when (target) {
            PracticeTab.HOME -> {
                activity.startActivity(
                    Intent(activity, DashboardHomeActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                )
                if (activity !is DashboardHomeActivity) activity.finish()
            }
            // Thư viện để trống (placeholder)
            PracticeTab.LIBRARY -> {
                activity.startActivity(
                    Intent(activity, PracticeDemoTabActivity::class.java)
                        .putExtra(EXTRA_TAB, PracticeTab.LIBRARY.name)
                )
                activity.finish()
            }
            // Danh mục khóa học nằm ở tab Luyện tập
            PracticeTab.PRACTICE -> {
                activity.startActivity(
                    Intent(activity, DashboardCategoryActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                )
                if (activity !is DashboardCategoryActivity) activity.finish()
            }
            PracticeTab.RANK -> {
                activity.startActivity(
                    Intent(activity, RankingActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                )
                if (activity !is RankingActivity) activity.finish()
            }
            PracticeTab.SETTINGS -> {
                activity.startActivity(
                    Intent(activity, PracticeDemoTabActivity::class.java)
                        .putExtra(EXTRA_TAB, target.name)
                )
                activity.finish()
            }
        }
    }

    private fun style(icon: ImageView, label: TextView, active: Boolean) {
        label.setTextColor(
            ContextCompat.getColor(
                label.context,
                if (active) R.color.practice_primary else R.color.practice_nav_inactive
            )
        )
        label.setTypeface(null, if (active) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL)
        (icon.parent as? LinearLayout)?.isSelected = active
    }

    private fun tint(icon: ImageView, active: Boolean) {
        icon.setColorFilter(
            ContextCompat.getColor(
                icon.context,
                if (active) R.color.practice_primary else R.color.practice_nav_inactive
            )
        )
    }
}
