package com.example.auraenglish.admin.helper

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.auraenglish.R
import com.example.auraenglish.admin.ui.AdminCourseManageActivity
import com.example.auraenglish.admin.ui.AdminExamShopActivity
import com.example.auraenglish.admin.ui.AdminStatisticsActivity
import com.example.auraenglish.admin.ui.AdminStudentManageActivity
import com.example.auraenglish.databinding.IncludeAdminBottomNavBinding
import com.example.auraenglish.practice.utils.setOnClickDebounced

enum class AdminTab {
    STATS,
    COURSES,
    EXAMS,
    STUDENTS
}

object AdminNavHelper {

    fun bind(
        activity: Activity,
        nav: IncludeAdminBottomNavBinding,
        activeTab: AdminTab
    ) {
        style(nav.ivNavStats, nav.tvNavStats, activeTab == AdminTab.STATS)
        style(nav.ivNavCourses, nav.tvNavCourses, activeTab == AdminTab.COURSES)
        style(nav.ivNavExams, nav.tvNavExams, activeTab == AdminTab.EXAMS)
        style(nav.ivNavStudents, nav.tvNavStudents, activeTab == AdminTab.STUDENTS)

        nav.navStats.setOnClickDebounced {
            open(activity, AdminTab.STATS, activeTab)
        }
        nav.navCourses.setOnClickDebounced {
            open(activity, AdminTab.COURSES, activeTab)
        }
        nav.navExams.setOnClickDebounced {
            open(activity, AdminTab.EXAMS, activeTab)
        }
        nav.navStudents.setOnClickDebounced {
            open(activity, AdminTab.STUDENTS, activeTab)
        }
    }

    private fun open(activity: Activity, target: AdminTab, current: AdminTab) {
        if (target == current) return
        val destination = when (target) {
            AdminTab.STATS -> AdminStatisticsActivity::class.java
            AdminTab.COURSES -> AdminCourseManageActivity::class.java
            AdminTab.EXAMS -> AdminExamShopActivity::class.java
            AdminTab.STUDENTS -> AdminStudentManageActivity::class.java
        }
        activity.startActivity(
            Intent(activity, destination).addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            )
        )
        activity.finish()
    }

    private fun style(icon: ImageView, label: TextView, active: Boolean) {
        val color = ContextCompat.getColor(
            label.context,
            if (active) R.color.admin_primary else R.color.admin_nav_inactive
        )
        icon.setColorFilter(color)
        label.setTextColor(color)
        label.setTypeface(null, if (active) Typeface.BOLD else Typeface.NORMAL)
    }
}
