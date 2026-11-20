package com.example.auraenglish.practice

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
import com.example.auraenglish.databinding.ActivityPracticeBookmarkedBinding
import com.example.auraenglish.practice.adapter.PracticeBookmarkedAdapter
import com.example.auraenglish.practice.utils.setOnClickDebounced
import com.example.auraenglish.practice.utils.showToast
import com.example.auraenglish.practice.utils.visibleIf
import com.example.auraenglish.practice.viewmodel.PracticeBookmarkedViewModel

class PracticeBookmarkedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPracticeBookmarkedBinding
    private val viewModel: PracticeBookmarkedViewModel by viewModels()
    private lateinit var bookmarkedAdapter: PracticeBookmarkedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPracticeBookmarkedBinding.inflate(layoutInflater)
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
        bookmarkedAdapter = PracticeBookmarkedAdapter { q ->
            val examId = intent.getStringExtra(PracticeSampleData.EXTRA_EXAM_ID)
                ?: PracticeSampleData.currentExamId
            startActivity(
                Intent(this, PracticePart5Activity::class.java)
                    .putExtra(PracticeSampleData.EXTRA_EXAM_ID, examId)
                    .putExtra(PracticeSampleData.EXTRA_QUESTION_NUMBER, q.number)
            )
        }
        binding.listContainer.apply {
            layoutManager = LinearLayoutManager(this@PracticeBookmarkedActivity)
            adapter = bookmarkedAdapter
            setHasFixedSize(true)
        }
        selectChip(binding.chipAll)
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickDebounced { finish() }
        binding.btnSearch.setOnClickDebounced {
            PracticeSearchHelper.show(
                activity = this,
                initialQuery = viewModel.searchQuery.value.orEmpty(),
                hint = getString(R.string.practice_search_hint_bookmark),
                onApply = { query -> viewModel.setSearchQuery(query) },
                onClear = {
                    viewModel.clearSearch()
                    showToast(R.string.practice_search_cleared)
                }
            )
        }
        binding.chipAll.setOnClickListener {
            viewModel.setFilter(ExamStatus.ALL)
            selectChip(binding.chipAll)
        }
        binding.chipTodo.setOnClickListener {
            viewModel.setFilter(ExamStatus.TODO)
            selectChip(binding.chipTodo)
        }
        binding.chipDone.setOnClickListener {
            viewModel.setFilter(ExamStatus.DONE)
            selectChip(binding.chipDone)
        }
    }

    private fun observeData() {
        viewModel.chipCounts.observe(this) { counts ->
            binding.chipAll.text = getString(R.string.practice_filter_flagged_all, counts.all)
            binding.chipTodo.text = getString(R.string.practice_filter_flagged_todo, counts.todo)
            binding.chipDone.text = getString(R.string.practice_filter_flagged_done, counts.done)
        }
        viewModel.questions.observe(this) { list ->
            bookmarkedAdapter.submitList(list)
            binding.tvEmpty.visibleIf(list.isEmpty())
            binding.listContainer.visibleIf(list.isNotEmpty())
        }
    }

    private fun selectChip(selected: TextView) {
        listOf(binding.chipAll, binding.chipTodo, binding.chipDone).forEach { chip ->
            val active = chip == selected
            chip.setBackgroundResource(
                if (active) R.drawable.bg_practice_chip_active else R.drawable.bg_practice_chip_inactive
            )
            chip.setTextColor(getColor(if (active) R.color.white else R.color.practice_primary))
        }
    }
}
