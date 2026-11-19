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
import com.example.auraenglish.databinding.ActivityPracticeResultBinding
import com.example.auraenglish.databinding.ItemPracticePartBinding

class PracticeResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPracticeResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPracticeResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        binding.tvScore.text = "685"
        binding.tvCorrectTotal.text = getString(R.string.practice_result_correct_total, 148, 200)
        binding.tvTime.text = getString(R.string.practice_result_time, "108:32")
        binding.tvListening.text = "340 / 495"
        binding.tvReading.text = "345 / 495"
        binding.tvStatCorrect.text = "${getString(R.string.practice_stat_correct)}\n148"
        binding.tvStatWrong.text = "${getString(R.string.practice_stat_wrong)}\n42"
        binding.tvStatSkip.text = "${getString(R.string.practice_stat_skip)}\n10"

        bindPart(binding.resultPart1, "1", getString(R.string.practice_part1), 5, 6, R.color.practice_part1)
        bindPart(binding.resultPart2, "2", getString(R.string.practice_part2), 21, 25, R.color.practice_part2)
        bindPart(binding.resultPart3, "3", getString(R.string.practice_part3), 28, 39, R.color.practice_part3)
        bindPart(binding.resultPart4, "4", getString(R.string.practice_part4), 22, 30, R.color.practice_part4)
        bindPart(binding.resultPart5, "5", getString(R.string.practice_part5), 24, 30, R.color.practice_part5)
        bindPart(binding.resultPart6, "6", getString(R.string.practice_part6), 11, 16, R.color.practice_part6)
        bindPart(binding.resultPart7, "7", getString(R.string.practice_part7), 37, 54, R.color.practice_part7)

        binding.btnClose.setOnClickListener { finish() }
        binding.btnRetry.setOnClickListener {
            startActivity(Intent(this, PracticeExamOverviewActivity::class.java))
            finish()
        }
        binding.btnViewAnswers.setOnClickListener {
            startActivity(Intent(this, PracticeAnswerReviewActivity::class.java))
        }
    }

    private fun bindPart(
        item: ItemPracticePartBinding,
        index: String,
        title: String,
        done: Int,
        total: Int,
        colorRes: Int
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
            val parentWidth = (item.progressFill.parent as View).width
            val width = (parentWidth * done / total.toFloat()).toInt()
            item.progressFill.layoutParams = item.progressFill.layoutParams.apply {
                this.width = width.coerceAtLeast(0)
            }
            item.progressFill.requestLayout()
        }
    }
}
