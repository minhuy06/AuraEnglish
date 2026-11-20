package com.example.auraenglish.practice.ui

import com.example.auraenglish.practice.utils.*

import com.example.auraenglish.practice.helper.*

import com.example.auraenglish.practice.data.*

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ActivityPracticeExamListBinding
import com.example.auraenglish.practice.adapter.PracticeExamAdapter
import com.example.auraenglish.practice.utils.setOnClickDebounced
import com.example.auraenglish.practice.utils.showToast
import com.example.auraenglish.practice.utils.visibleIf
import com.example.auraenglish.practice.viewmodel.PracticeExamListViewModel

class PracticeExamListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPracticeExamListBinding
    private val viewModel: PracticeExamListViewModel by viewModels()
    private lateinit var examAdapter: PracticeExamAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPracticeExamListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupViews()
        setupListeners()
        observeData()
        viewModel.refresh()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }

    private fun setupViews() {
        examAdapter = PracticeExamAdapter { exam ->
            PracticeSampleData.currentExamId = exam.id
            startActivity(
                Intent(this, PracticeExamOverviewActivity::class.java)
                    .putExtra(PracticeSampleData.EXTRA_EXAM_ID, exam.id)
            )
        }
        binding.examList.apply {
            layoutManager = LinearLayoutManager(this@PracticeExamListActivity)
            adapter = examAdapter
            setHasFixedSize(true)
        }
        PracticeNavHelper.bind(this, binding.bottomNavInclude, PracticeTab.PRACTICE)
        selectChip(binding.chipAll, chipViews())
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickDebounced {
            showToast(R.string.practice_demo_list_back)
        }
        binding.btnSearch.setOnClickDebounced { openSearch() }

        val map = mapOf(
            binding.chipAll to ExamStatus.ALL,
            binding.chipTodo to ExamStatus.TODO,
            binding.chipDoing to ExamStatus.DOING,
            binding.chipDone to ExamStatus.DONE
        )
        map.forEach { (chip, status) ->
            chip.setOnClickListener {
                viewModel.setFilter(status)
                selectChip(chip, chipViews())
            }
        }
    }

    private fun observeData() {
        viewModel.exams.observe(this) { exams ->
            examAdapter.submitList(exams)
            binding.tvEmpty.visibleIf(exams.isEmpty())
            binding.examList.visibleIf(exams.isNotEmpty())
        }
    }

    private fun openSearch() {
        PracticeSearchHelper.show(
            activity = this,
            initialQuery = viewModel.searchQuery.value.orEmpty(),
            hint = getString(R.string.practice_search_hint_exam),
            onApply = { query ->
                viewModel.setSearchQuery(query)
                if (query.isNotBlank()) {
                    showToast(getString(R.string.practice_search_applied, query))
                }
            },
            onClear = {
                viewModel.clearSearch()
                showToast(R.string.practice_search_cleared)
            }
        )
    }

    private fun chipViews(): List<TextView> =
        listOf(binding.chipAll, binding.chipTodo, binding.chipDoing, binding.chipDone)

    private fun selectChip(selected: TextView, all: List<TextView>) {
        all.forEach { chip ->
            val active = chip == selected
            chip.setBackgroundResource(
                if (active) R.drawable.bg_practice_chip_active else R.drawable.bg_practice_chip_inactive
            )
            chip.setTextColor(getColor(if (active) R.color.white else R.color.practice_primary))
            chip.setTypeface(
                null,
                if (active) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL
            )
        }
    }
}
