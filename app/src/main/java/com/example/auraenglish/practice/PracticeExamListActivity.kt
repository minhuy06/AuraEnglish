package com.example.auraenglish.practice

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ActivityPracticeExamListBinding
import com.example.auraenglish.databinding.ItemPracticeExamBinding

class PracticeExamListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPracticeExamListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPracticeExamListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        bindExam(binding.itemExam1, "03", getString(R.string.practice_status_doing, 45), R.drawable.bg_practice_status_doing, R.color.practice_chip_progress_text)
        bindExam(binding.itemExam2, "04", getString(R.string.practice_status_todo), R.drawable.bg_practice_status_todo, R.color.practice_text_secondary)
        bindExam(binding.itemExam3, "05", getString(R.string.practice_status_done, 720), R.drawable.bg_practice_status_done, R.color.practice_chip_done_text)

        setupFilters()
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun bindExam(
        item: ItemPracticeExamBinding,
        index: String,
        status: String,
        statusBg: Int,
        statusColor: Int
    ) {
        item.tvIndex.text = index
        item.tvTitle.text = getString(R.string.practice_exam_title, index)
        item.tvStatus.text = status
        item.tvStatus.setBackgroundResource(statusBg)
        item.tvStatus.setTextColor(getColor(statusColor))
        item.root.setOnClickListener {
            startActivity(Intent(this, PracticeExamOverviewActivity::class.java))
        }
    }

    private fun setupFilters() {
        val chips = listOf(binding.chipAll, binding.chipTodo, binding.chipDoing, binding.chipDone)
        chips.forEach { chip ->
            chip.setOnClickListener { selectChip(chip, chips) }
        }
    }

    private fun selectChip(selected: TextView, all: List<TextView>) {
        all.forEach { chip ->
            val active = chip == selected
            chip.setBackgroundResource(
                if (active) R.drawable.bg_practice_chip_active else R.drawable.bg_practice_chip_inactive
            )
            chip.setTextColor(getColor(if (active) R.color.white else R.color.practice_text_secondary))
        }
    }
}
