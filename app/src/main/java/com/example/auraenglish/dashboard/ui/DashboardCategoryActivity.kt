package com.example.auraenglish.dashboard.ui

import android.content.Intent
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.auraenglish.R
import com.example.auraenglish.dashboard.adapter.DashboardCourseAdapter
import com.example.auraenglish.dashboard.adapter.DashboardSectionAdapter
import com.example.auraenglish.dashboard.data.CourseFilter
import com.example.auraenglish.dashboard.data.DashboardSampleData
import com.example.auraenglish.databinding.ActivityDashboardCategoryBinding
import com.example.auraenglish.listening.adapter.ListeningExamAdapter
import com.example.auraenglish.listening.data.ListeningSampleData
import com.example.auraenglish.listening.ui.ListeningQuestionActivity
import com.example.auraenglish.practice.adapter.PracticeExamAdapter
import com.example.auraenglish.practice.data.ExamStatus
import com.example.auraenglish.practice.data.PracticeExam
import com.example.auraenglish.practice.data.PracticeSampleData
import com.example.auraenglish.practice.helper.PracticeNavHelper
import com.example.auraenglish.practice.helper.PracticeTab
import com.example.auraenglish.practice.ui.PracticeExamOverviewActivity

// Danh mục: khóa học + đề practice + luyện nghe
class DashboardCategoryActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_FILTER = "extra_course_filter"
    }

    private lateinit var binding: ActivityDashboardCategoryBinding
    private lateinit var courseAdapter: DashboardCourseAdapter
    private lateinit var examAdapter: PracticeExamAdapter
    private lateinit var listenAdapter: ListeningExamAdapter
    private lateinit var examHeaderAdapter: DashboardSectionAdapter
    private var filter = CourseFilter.ALL
    private var query = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDashboardCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        filter = runCatching {
            CourseFilter.valueOf(intent.getStringExtra(EXTRA_FILTER) ?: CourseFilter.ALL.name)
        }.getOrDefault(CourseFilter.ALL)

        courseAdapter = DashboardCourseAdapter()
        examHeaderAdapter = DashboardSectionAdapter()
        examAdapter = PracticeExamAdapter { exam -> openExam(exam) }
        listenAdapter = ListeningExamAdapter { exam ->
            startActivity(
                Intent(this, ListeningQuestionActivity::class.java)
                    .putExtra(ListeningSampleData.EXTRA_EXAM_ID, exam.id)
            )
        }

        binding.courseList.layoutManager = LinearLayoutManager(this)
        binding.courseList.setHasFixedSize(true)

        PracticeNavHelper.bind(this, binding.bottomNavInclude, PracticeTab.PRACTICE)

        val chips = listOf(
            binding.chipAll to CourseFilter.ALL,
            binding.chipVocab to CourseFilter.VOCAB,
            binding.chipGrammar to CourseFilter.GRAMMAR,
            binding.chipExam to CourseFilter.EXAM,
            binding.chipListen to CourseFilter.LISTEN
        )
        chips.forEach { (chip, value) ->
            chip.setOnClickListener {
                filter = value
                selectChip(chip, chips.map { it.first })
                refresh(animate = true)
            }
        }

        binding.etSearch.doAfterTextChanged {
            query = it?.toString().orEmpty().trim()
            refresh(animate = true)
        }

        val initialChip = chips.firstOrNull { it.second == filter }?.first ?: binding.chipAll
        selectChip(initialChip, chips.map { it.first })
        refresh(animate = false)
    }

    override fun onResume() {
        super.onResume()
        if (filter == CourseFilter.ALL || filter == CourseFilter.EXAM || filter == CourseFilter.LISTEN) {
            refresh(animate = false)
        }
    }

    private fun openExam(exam: PracticeExam) {
        PracticeSampleData.currentExamId = exam.id
        startActivity(
            Intent(this, PracticeExamOverviewActivity::class.java)
                .putExtra(PracticeSampleData.EXTRA_EXAM_ID, exam.id)
        )
    }

    private fun filteredExams(): List<PracticeExam> {
        return PracticeSampleData.filteredExams(ExamStatus.ALL).filter { exam ->
            if (query.isBlank()) true
            else {
                val q = query.lowercase()
                exam.title.lowercase().contains(q) ||
                    exam.index.lowercase().contains(q) ||
                    exam.meta.lowercase().contains(q)
            }
        }
    }

    private fun filteredListen() = ListeningSampleData.exams.filter { exam ->
        if (query.isBlank()) true
        else {
            val q = query.lowercase()
            exam.title.lowercase().contains(q) || exam.index.lowercase().contains(q)
        }
    }

    private fun refresh(animate: Boolean) {
        var itemCount = 0
        when (filter) {
            CourseFilter.EXAM -> {
                binding.etSearch.hint = getString(R.string.practice_search_hint_exam)
                examHeaderAdapter.setTitle("")
                val exams = filteredExams()
                examAdapter.submitList(exams)
                binding.courseList.adapter = examAdapter
                itemCount = exams.size
            }
            CourseFilter.LISTEN -> {
                binding.etSearch.hint = getString(R.string.listen_title)
                ListeningSampleData.exams.forEach { ListeningSampleData.refreshProgress(it) }
                val items = filteredListen()
                listenAdapter.submitList(items.toList())
                binding.courseList.adapter = listenAdapter
                itemCount = items.size
            }
            CourseFilter.ALL -> {
                binding.etSearch.hint = getString(R.string.dash_search_all)
                val courses = DashboardSampleData.filteredCourses(CourseFilter.ALL, query)
                val exams = filteredExams()
                courseAdapter.submitList(courses)
                examHeaderAdapter.setTitle(
                    if (exams.isEmpty()) "" else getString(R.string.dash_section_exams)
                )
                examAdapter.submitList(exams)
                binding.courseList.adapter = ConcatAdapter(
                    courseAdapter,
                    examHeaderAdapter,
                    examAdapter
                )
                itemCount = courses.size + exams.size
            }
            else -> {
                binding.etSearch.hint = getString(R.string.dash_search_courses)
                examHeaderAdapter.setTitle("")
                val courses = DashboardSampleData.filteredCourses(filter, query)
                courseAdapter.submitList(courses)
                binding.courseList.adapter = courseAdapter
                itemCount = courses.size
            }
        }

        val empty = itemCount == 0
        binding.emptyState.isVisible = empty
        binding.courseList.isVisible = !empty
        binding.tvEmptyTip.text = when {
            query.isNotBlank() -> getString(R.string.dash_empty_tip)
            filter == CourseFilter.EXAM || filter == CourseFilter.LISTEN ->
                getString(R.string.dash_empty_tip)
            else -> getString(R.string.dash_empty_library_tip)
        }

        if (animate && !empty) {
            binding.courseList.alpha = 0f
            binding.courseList.translationY = 16f
            binding.courseList.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(240)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }
    }

    private fun selectChip(selected: TextView, all: List<TextView>) {
        all.forEach { chip ->
            val active = chip == selected
            chip.setBackgroundResource(
                if (active) R.drawable.bg_dash_chip_active else R.drawable.bg_dash_chip_idle
            )
            chip.setTextColor(
                ContextCompat.getColor(
                    this,
                    if (active) R.color.white else R.color.dash_primary
                )
            )
            chip.setTypeface(
                null,
                if (active) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL
            )
        }
    }
}
