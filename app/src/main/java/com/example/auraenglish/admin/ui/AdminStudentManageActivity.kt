package com.example.auraenglish.admin.ui

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.auraenglish.R
import com.example.auraenglish.admin.adapter.AdminStudentAdapter
import com.example.auraenglish.admin.data.AdminStudentFilter
import com.example.auraenglish.admin.data.AdminStudentUiModel
import com.example.auraenglish.admin.viewmodel.AdminStudentListState
import com.example.auraenglish.admin.viewmodel.AdminStudentViewModel
import com.example.auraenglish.databinding.ActivityAdminStudentManageBinding
import com.example.auraenglish.practice.utils.setOnClickDebounced
import com.example.auraenglish.practice.utils.showToast
import com.example.auraenglish.practice.utils.visibleIf
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AdminStudentManageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminStudentManageBinding
    private val viewModel: AdminStudentViewModel by viewModels()
    private lateinit var studentAdapter: AdminStudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminStudentManageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupStudentList()
        setupListeners()
        observeViewModel()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.rootAdminStudentManage) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }

    private fun setupStudentList() {
        studentAdapter = AdminStudentAdapter(
            onProfileClick = ::showStudentProfile,
            onStatusAction = ::confirmStatusChange
        )
        binding.rvAdminStudents.apply {
            layoutManager = LinearLayoutManager(this@AdminStudentManageActivity)
            setHasFixedSize(true)
            adapter = studentAdapter
        }
    }

    private fun setupListeners() = with(binding) {
        edtSearchStudent.doAfterTextChanged { text ->
            viewModel.setSearchQuery(text?.toString().orEmpty())
        }

        chipStudentAll.setOnClickListener {
            viewModel.setFilter(AdminStudentFilter.ALL)
        }
        chipStudentActive.setOnClickListener {
            viewModel.setFilter(AdminStudentFilter.ACTIVE)
        }
        chipStudentRisk.setOnClickListener {
            viewModel.setFilter(AdminStudentFilter.AT_RISK)
        }
        chipStudentSuspended.setOnClickListener {
            viewModel.setFilter(AdminStudentFilter.SUSPENDED)
        }

        includeAdminBottomNav.navStats.setOnClickDebounced {
            openAdminScreen(AdminStatisticsActivity::class.java)
        }
        includeAdminBottomNav.navCourses.setOnClickDebounced {
            openAdminScreen(AdminCourseManageActivity::class.java)
        }
        includeAdminBottomNav.navExams.setOnClickDebounced {
            openAdminScreen(AdminExamShopActivity::class.java)
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(this) { state ->
            bindState(state)
        }
    }

    private fun bindState(state: AdminStudentListState) = with(binding) {
        tvStudentsSummary.text = getString(
            R.string.admin_students_summary_fmt,
            state.allCount,
            state.atRiskCount
        )
        chipStudentAll.text = getString(
            R.string.admin_students_filter_all_fmt,
            state.allCount
        )
        chipStudentActive.text = getString(
            R.string.admin_students_filter_active_fmt,
            state.activeCount
        )
        chipStudentRisk.text = getString(
            R.string.admin_students_filter_risk_fmt,
            state.atRiskCount
        )
        chipStudentSuspended.text = getString(
            R.string.admin_students_filter_suspended_fmt,
            state.suspendedCount
        )

        styleFilterChip(chipStudentAll, state.filter == AdminStudentFilter.ALL)
        styleFilterChip(chipStudentActive, state.filter == AdminStudentFilter.ACTIVE)
        styleFilterChip(chipStudentRisk, state.filter == AdminStudentFilter.AT_RISK)
        styleFilterChip(chipStudentSuspended, state.filter == AdminStudentFilter.SUSPENDED)

        studentAdapter.submitList(state.students)
        rvAdminStudents.visibleIf(state.students.isNotEmpty())
        tvEmptyAdminStudents.visibleIf(state.students.isEmpty())
    }

    private fun styleFilterChip(chip: TextView, active: Boolean) {
        chip.setBackgroundResource(
            if (active) R.drawable.bg_admin_chip_active else R.drawable.bg_admin_chip_inactive
        )
        chip.setTextColor(
            ContextCompat.getColor(
                this,
                if (active) R.color.white else R.color.admin_text_secondary
            )
        )
        chip.setTypeface(null, if (active) Typeface.BOLD else Typeface.NORMAL)
    }

    private fun showStudentProfile(student: AdminStudentUiModel) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.admin_student_profile_title)
            .setMessage(
                getString(
                    R.string.admin_student_profile_message_fmt,
                    student.email,
                    student.currentCourse,
                    student.joinedDate,
                    student.completedLessons,
                    student.totalLessons,
                    student.progressPercent,
                    student.streakDays,
                    student.lastActive
                )
            )
            .setPositiveButton(R.string.action_close) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun confirmStatusChange(student: AdminStudentUiModel) {
        val willSuspend = !student.isSuspended
        val titleRes = if (willSuspend) {
            R.string.admin_student_suspend_title
        } else {
            R.string.admin_student_reactivate_title
        }
        val messageRes = if (willSuspend) {
            R.string.admin_student_suspend_message_fmt
        } else {
            R.string.admin_student_reactivate_message_fmt
        }
        val actionRes = if (willSuspend) {
            R.string.admin_student_action_suspend
        } else {
            R.string.admin_student_action_reactivate
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(titleRes)
            .setMessage(getString(messageRes, student.fullName))
            .setCancelable(false)
            .setNegativeButton(R.string.action_cancel) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(actionRes) { dialog, _ ->
                dialog.dismiss()
                viewModel.setSuspended(student.id, willSuspend)
                showToast(
                    getString(
                        if (willSuspend) {
                            R.string.admin_student_toast_suspended_fmt
                        } else {
                            R.string.admin_student_toast_reactivated_fmt
                        },
                        student.fullName
                    )
                )
            }
            .show()
    }

    private fun openAdminScreen(target: Class<out AppCompatActivity>) {
        startActivity(
            Intent(this, target).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
        finish()
    }
}
