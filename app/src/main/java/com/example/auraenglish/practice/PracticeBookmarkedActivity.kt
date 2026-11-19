package com.example.auraenglish.practice

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ActivityPracticeBookmarkedBinding
import com.example.auraenglish.databinding.ItemPracticeBookmarkedBinding

class PracticeBookmarkedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPracticeBookmarkedBinding
    private var filter = ExamStatus.ALL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPracticeBookmarkedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        setupFilters()
        renderList()
        binding.btnBack.setOnClickListener { finish() }
        binding.btnSearch.setOnClickListener { /* no-op sample */ }
    }

    override fun onResume() {
        super.onResume()
        renderList()
        updateChipLabels()
    }

    private fun setupFilters() {
        updateChipLabels()
        selectChip(binding.chipAll)
        binding.chipAll.setOnClickListener {
            filter = ExamStatus.ALL
            selectChip(binding.chipAll)
            renderList()
        }
        binding.chipTodo.setOnClickListener {
            filter = ExamStatus.TODO
            selectChip(binding.chipTodo)
            renderList()
        }
        binding.chipDone.setOnClickListener {
            filter = ExamStatus.DONE
            selectChip(binding.chipDone)
            renderList()
        }
    }

    private fun updateChipLabels() {
        val all = PracticeSampleData.flaggedQuestions(ExamStatus.ALL)
        val todo = PracticeSampleData.flaggedQuestions(ExamStatus.TODO)
        val done = PracticeSampleData.flaggedQuestions(ExamStatus.DONE)
        binding.chipAll.text = getString(R.string.practice_filter_flagged_all, all.size)
        binding.chipTodo.text = getString(R.string.practice_filter_flagged_todo, todo.size)
        binding.chipDone.text = getString(R.string.practice_filter_flagged_done, done.size)
    }

    private fun selectChip(selected: TextView) {
        listOf(binding.chipAll, binding.chipTodo, binding.chipDone).forEach { chip ->
            val active = chip == selected
            chip.setBackgroundResource(
                if (active) R.drawable.bg_practice_chip_active else R.drawable.bg_practice_chip_inactive
            )
            chip.setTextColor(getColor(if (active) R.color.white else R.color.practice_text_secondary))
        }
    }

    private fun renderList() {
        binding.listContainer.removeAllViews()
        val data = PracticeSampleData.flaggedQuestions(filter)
        if (data.isEmpty()) {
            val empty = TextView(this).apply {
                text = "Chưa có câu đánh dấu"
                setTextColor(getColor(R.color.practice_text_secondary))
                textSize = 14f
                setPadding(8, 40, 8, 8)
            }
            binding.listContainer.addView(empty)
            return
        }
        data.forEach { q ->
            val item = ItemPracticeBookmarkedBinding.inflate(
                LayoutInflater.from(this), binding.listContainer, false
            )
            item.tvNumber.text = q.number.toString()
            item.tvTag.text = q.tag
            item.tvQuestion.text = q.stem
            if (q.isAnswered) {
                val letter = ('A' + (q.userAnswerIndex ?: 0)).toString()
                item.tvStatus.text = getString(R.string.practice_selected_answer, letter)
                item.tvStatus.setBackgroundResource(R.drawable.bg_practice_status_doing)
                item.tvStatus.setTextColor(getColor(R.color.practice_chip_progress_text))
            } else {
                item.tvStatus.text = getString(R.string.practice_status_todo)
                item.tvStatus.setBackgroundResource(R.drawable.bg_practice_status_todo)
                item.tvStatus.setTextColor(getColor(R.color.practice_pending_text))
            }
            item.root.isClickable = true
            item.root.setOnClickListener {
                val examId = intent.getStringExtra(PracticeSampleData.EXTRA_EXAM_ID)
                    ?: PracticeSampleData.currentExamId
                startActivity(
                    Intent(this, PracticePart5Activity::class.java)
                        .putExtra(PracticeSampleData.EXTRA_EXAM_ID, examId)
                        .putExtra(PracticeSampleData.EXTRA_QUESTION_NUMBER, q.number)
                )
            }
            binding.listContainer.addView(item.root)
        }
    }
}
