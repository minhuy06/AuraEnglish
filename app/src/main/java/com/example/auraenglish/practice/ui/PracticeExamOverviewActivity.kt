package com.example.auraenglish.practice.ui

import com.example.auraenglish.practice.utils.*

import com.example.auraenglish.practice.helper.*

import com.example.auraenglish.practice.data.*

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ActivityPracticeExamOverviewBinding
import com.example.auraenglish.databinding.ItemPracticePartBinding

// Tổng quan đề (7 Part) — chỉ Part 5 làm được
class PracticeExamOverviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPracticeExamOverviewBinding
    private lateinit var exam: PracticeExam

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPracticeExamOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        val examId = intent.getStringExtra(PracticeSampleData.EXTRA_EXAM_ID)
            ?: PracticeSampleData.currentExamId
        exam = PracticeSampleData.examById(examId)
        PracticeSampleData.currentExamId = exam.id

        val title = getString(R.string.practice_exam_detail_title, exam.index)
        binding.tvExamTitle.text = title
        binding.tvHeroTitle.text = title

        val partBindings = listOf(
            binding.part1, binding.part2, binding.part3, binding.part4,
            binding.part5, binding.part6, binding.part7
        )
        exam.parts.forEachIndexed { index, part ->
            bindPart(partBindings[index], part)
        }

        binding.btnContinue.text = if (exam.status == ExamStatus.TODO) {
            getString(R.string.practice_start)
        } else {
            getString(R.string.practice_continue)
        }

        binding.btnContinue.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_practice_play, 0, 0, 0)
        binding.btnContinue.compoundDrawablePadding = (8 * resources.displayMetrics.density).toInt()

        binding.btnBack.setOnClickListener { finish() }
        binding.btnSearch.setOnClickListener {
            PracticeSearchHelper.show(
                activity = this,
                hint = "Ví dụ: Part 5, đọc hiểu…",
                onApply = { query ->
                    val matched = exam.parts.filter {
                        it.title.contains(query, ignoreCase = true) ||
                            it.number.toString() == query.trim()
                    }
                    if (query.isBlank()) {
                        Toast.makeText(this, "Hiển thị tất cả Part", Toast.LENGTH_SHORT).show()
                    } else if (matched.isEmpty()) {
                        Toast.makeText(this, R.string.practice_search_empty, Toast.LENGTH_SHORT).show()
                    } else {
                        val names = matched.joinToString { "Part ${it.number}" }
                        Toast.makeText(this, "Tìm thấy: $names", Toast.LENGTH_LONG).show()
                        matched.firstOrNull { it.number == 5 }?.let { openPart(5) }
                    }
                },
                onClear = {
                    Toast.makeText(this, "Đã xóa tìm kiếm Part", Toast.LENGTH_SHORT).show()
                }
            )
        }
        binding.btnContinue.setOnClickListener { openPart(5) }
        binding.btnFinishExam.setOnClickListener {
            val unanswered = PracticeSampleData.part5Questions.count { !it.isAnswered }
            PracticeConfirmHelper.showSubmitExam(
                activity = this,
                unansweredCount = unanswered
            ) {
                startActivity(
                    Intent(this, PracticeResultActivity::class.java)
                        .putExtra(PracticeSampleData.EXTRA_EXAM_ID, exam.id)
                )
            }
        }
    }

    // Bind 1 hàng Part: index màu, progress width theo done/total.
    private fun bindPart(item: ItemPracticePartBinding, part: PracticePart) {
        item.tvPartIndex.text = part.number.toString()
        item.tvPartTitle.text = part.title
        item.tvPartProgress.text = getString(R.string.practice_part_progress, part.done, part.total)
        val colorId = resources.getIdentifier(part.colorResName, "color", packageName)
        val bg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 12f * resources.displayMetrics.density
            setColor(getColor(if (colorId != 0) colorId else R.color.practice_primary))
        }
        item.tvPartIndex.background = bg
        item.progressFill.post {
            val parentWidth = (item.progressFill.parent as View).width
            val width = (parentWidth * part.done / part.total.coerceAtLeast(1).toFloat()).toInt()
            item.progressFill.layoutParams = item.progressFill.layoutParams.apply {
                this.width = width.coerceAtLeast(0)
            }
            item.progressFill.requestLayout()
        }
        item.root.isClickable = true
        item.root.isFocusable = true
        item.root.setOnClickListener {
            if (part.number == 5) {
                openPart(5)
            } else {
                Toast.makeText(this, "${part.title} (UI Part 5 demo)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Mở Part 5 (demo).
    private fun openPart(partNumber: Int) {
        startActivity(
            Intent(this, PracticePart5Activity::class.java)
                .putExtra(PracticeSampleData.EXTRA_EXAM_ID, exam.id)
                .putExtra(PracticeSampleData.EXTRA_PART_NUMBER, partNumber)
                .putExtra(PracticeSampleData.EXTRA_QUESTION_NUMBER, 12)
        )
    }
}
