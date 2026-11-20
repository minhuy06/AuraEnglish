package com.example.auraenglish.dashboard.ui

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.auraenglish.R
import com.example.auraenglish.dashboard.adapter.DashboardCourseAdapter
import com.example.auraenglish.dashboard.data.CourseFilter
import com.example.auraenglish.dashboard.data.DashboardSampleData
import com.example.auraenglish.databinding.ActivityDashboardCategoryBinding
import com.example.auraenglish.practice.helper.PracticeNavHelper
import com.example.auraenglish.practice.helper.PracticeTab

// Danh mục bài học + 4 filter pills (Figma)
class DashboardCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardCategoryBinding
    private lateinit var adapter: DashboardCourseAdapter
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

        adapter = DashboardCourseAdapter()
        binding.courseList.layoutManager = LinearLayoutManager(this)
        binding.courseList.adapter = adapter
        binding.courseList.setHasFixedSize(true)

        PracticeNavHelper.bind(this, binding.bottomNavInclude, PracticeTab.LIBRARY)

        val chips = listOf(
            binding.chipAll to CourseFilter.ALL,
            binding.chipVocab to CourseFilter.VOCAB,
            binding.chipGrammar to CourseFilter.GRAMMAR,
            binding.chipExam to CourseFilter.EXAM
        )
        chips.forEach { (chip, value) ->
            chip.setOnClickListener {
                filter = value
                selectChip(chip, chips.map { it.first })
                refresh()
            }
        }

        binding.etSearch.doAfterTextChanged {
            query = it?.toString().orEmpty().trim()
            refresh()
        }

        selectChip(binding.chipAll, chips.map { it.first })
        refresh()
    }

    private fun refresh() {
        adapter.submitList(DashboardSampleData.filteredCourses(filter, query))
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
