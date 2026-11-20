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
import com.example.auraenglish.admin.adapter.AdminCourseAdapter
import com.example.auraenglish.admin.data.AdminCourseSkill
import com.example.auraenglish.admin.data.AdminCourseUiModel
import com.example.auraenglish.admin.helper.AdminCourseDialogHelper
import com.example.auraenglish.admin.viewmodel.AdminCourseListState
import com.example.auraenglish.admin.viewmodel.AdminCourseViewModel
import com.example.auraenglish.databinding.ActivityAdminCourseManageBinding
import com.example.auraenglish.practice.utils.setOnClickDebounced
import com.example.auraenglish.practice.utils.showToast
import com.example.auraenglish.practice.utils.visibleIf
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AdminCourseManageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminCourseManageBinding
    private val viewModel: AdminCourseViewModel by viewModels()
    private lateinit var courseAdapter: AdminCourseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminCourseManageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeViewModel()
        setupListeners()
    }

    private fun setupViews() = with(binding) {
        ViewCompat.setOnApplyWindowInsetsListener(rootAdminCourseManage) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        // Highlight tab Khóa học trong Admin Bottom Nav
        val primary = ContextCompat.getColor(this@AdminCourseManageActivity, R.color.admin_primary)
        val inactive = ContextCompat.getColor(this@AdminCourseManageActivity, R.color.admin_nav_inactive)
        includeAdminBottomNav.tvNavCourses.setTextColor(primary)
        includeAdminBottomNav.tvNavCourses.setTypeface(null, Typeface.BOLD)
        includeAdminBottomNav.ivNavCourses.setColorFilter(primary)
        includeAdminBottomNav.tvNavExams.setTextColor(inactive)
        includeAdminBottomNav.tvNavExams.setTypeface(null, Typeface.NORMAL)
        includeAdminBottomNav.ivNavExams.setColorFilter(inactive)

        courseAdapter = AdminCourseAdapter(
            onPrimaryAction = { course -> handlePrimaryAction(course) },
            onEditClick = { course -> openEditDialog(course) },
            onDangerAction = { course -> handleDangerAction(course) }
        )

        rvAdminCourses.apply {
            layoutManager = LinearLayoutManager(this@AdminCourseManageActivity)
            setHasFixedSize(true)
            adapter = courseAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(this) { state ->
            bindState(state)
        }
    }

    private fun bindState(state: AdminCourseListState) = with(binding) {
        chipCourseAll.text = getString(R.string.admin_course_filter_all_fmt, state.allCount)
        chipCourseVocab.text = getString(R.string.admin_course_filter_vocab_fmt, state.vocabCount)
        chipCourseGrammar.text = getString(R.string.admin_course_filter_grammar_fmt, state.grammarCount)
        chipCourseListen.text = getString(R.string.admin_course_filter_listen_fmt, state.listenCount)

        styleChip(chipCourseAll, state.filter == AdminCourseSkill.ALL)
        styleChip(chipCourseVocab, state.filter == AdminCourseSkill.VOCAB)
        styleChip(chipCourseGrammar, state.filter == AdminCourseSkill.GRAMMAR)
        styleChip(chipCourseListen, state.filter == AdminCourseSkill.LISTEN)

        courseAdapter.submitList(state.courses)
        tvEmptyAdminCourses.visibleIf(state.courses.isEmpty())
    }

    private fun styleChip(chip: TextView, active: Boolean) {
        chip.setBackgroundResource(if (active) R.drawable.bg_admin_chip_active else R.drawable.bg_admin_chip_inactive)
        chip.setTextColor(
            ContextCompat.getColor(
                this,
                if (active) R.color.white else R.color.admin_text_secondary
            )
        )
    }

    private fun setupListeners() = with(binding) {
        edtSearchCourse.doAfterTextChanged { text ->
            viewModel.setSearchQuery(text?.toString().orEmpty())
        }

        chipCourseAll.setOnClickListener { viewModel.setFilter(AdminCourseSkill.ALL) }
        chipCourseVocab.setOnClickListener { viewModel.setFilter(AdminCourseSkill.VOCAB) }
        chipCourseGrammar.setOnClickListener { viewModel.setFilter(AdminCourseSkill.GRAMMAR) }
        chipCourseListen.setOnClickListener { viewModel.setFilter(AdminCourseSkill.LISTEN) }

        btnAddNewCourse.setOnClickDebounced {
            AdminCourseDialogHelper.showCourseDialog(this@AdminCourseManageActivity) { title, skill, cefr, meta ->
                viewModel.addCourse(title, skill, cefr, meta)
                showToast(getString(R.string.admin_toast_course_created, title))
            }
        }

        includeAdminBottomNav.navExams.setOnClickListener {
            startActivity(
                Intent(this@AdminCourseManageActivity, AdminExamShopActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
            finish()
        }
    }

    private fun handlePrimaryAction(course: AdminCourseUiModel) {
        if (!course.isPublished) {
            viewModel.setPublished(course.id, true)
            showToast(getString(R.string.admin_toast_course_published, course.title))
        } else {
            showToast("${course.title} (${course.skill.label})")
        }
    }

    private fun openEditDialog(course: AdminCourseUiModel) {
        AdminCourseDialogHelper.showCourseDialog(this, course) { title, skill, cefr, meta ->
            viewModel.updateCourse(course.id, title, skill, cefr, meta)
            showToast(getString(R.string.admin_toast_course_updated, title))
        }
    }

    private fun handleDangerAction(course: AdminCourseUiModel) {
        if (course.isPublished) {
            viewModel.setPublished(course.id, false)
            showToast(getString(R.string.admin_toast_course_hidden, course.title))
        } else {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.admin_course_action_delete)
                .setMessage(getString(R.string.popup_delete_message, course.title))
                .setNegativeButton(R.string.admin_btn_cancel_course, null)
                .setPositiveButton(R.string.admin_course_action_delete) { dialog, _ ->
                    dialog.dismiss()
                    viewModel.deleteCourse(course.id)
                    showToast(getString(R.string.admin_toast_course_deleted, course.title))
                }
                .show()
        }
    }
}
