package com.example.auraenglish.practice

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ActivityPracticeExamOverviewBinding
import com.example.auraenglish.databinding.ItemPracticePartBinding

class PracticeExamOverviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPracticeExamOverviewBinding

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

        val examTitle = getString(R.string.practice_exam_detail_title, "01")
        binding.tvExamTitle.text = examTitle
        binding.tvHeroTitle.text = examTitle
        bindPart(binding.part1, "1", getString(R.string.practice_part1), 6, 6, R.color.practice_part1)
        bindPart(binding.part2, "2", getString(R.string.practice_part2), 18, 25, R.color.practice_part2)
        bindPart(binding.part3, "3", getString(R.string.practice_part3), 12, 39, R.color.practice_part3)
        bindPart(binding.part4, "4", getString(R.string.practice_part4), 0, 30, R.color.practice_part4)
        bindPart(binding.part5, "5", getString(R.string.practice_part5), 11, 30, R.color.practice_part5) {
            startActivity(Intent(this, PracticePart5Activity::class.java))
        }
        bindPart(binding.part6, "6", getString(R.string.practice_part6), 0, 15, R.color.practice_part6)
        bindPart(binding.part7, "7", getString(R.string.practice_part7), 5, 54, R.color.practice_part7)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnContinue.setOnClickListener {
            startActivity(Intent(this, PracticePart5Activity::class.java))
        }
        binding.btnFinishExam.setOnClickListener {
            startActivity(Intent(this, PracticeResultActivity::class.java))
        }
    }

    private fun bindPart(
        item: ItemPracticePartBinding,
        index: String,
        title: String,
        done: Int,
        total: Int,
        colorRes: Int,
        onClick: (() -> Unit)? = null
    ) {
        item.tvPartIndex.text = index
        item.tvPartTitle.text = title
        item.tvPartProgress.text = getString(R.string.practice_part_progress, done, total)
        val bg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 12f * resources.displayMetrics.density
            setColor(getColor(colorRes))
        }
        item.tvPartIndex.background = bg
        item.progressFill.post {
            val width = ((item.progressFill.parent as View).width * done / total.toFloat()).toInt()
            item.progressFill.layoutParams = item.progressFill.layoutParams.apply { this.width = width.coerceAtLeast(0) }
            item.progressFill.requestLayout()
        }
        item.root.setOnClickListener { onClick?.invoke() }
    }
}
