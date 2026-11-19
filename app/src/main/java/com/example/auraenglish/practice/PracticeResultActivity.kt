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
    private lateinit var exam: PracticeExam

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

        val examId = intent.getStringExtra(PracticeSampleData.EXTRA_EXAM_ID)
            ?: PracticeSampleData.currentExamId
        exam = PracticeSampleData.examById(examId)
        val result = PracticeSampleData.result

        binding.tvScore.text = result.score.toString()
        binding.tvCorrectTotal.text = getString(
            R.string.practice_result_correct_total, result.correct, result.total
        )
        binding.tvTime.text = getString(R.string.practice_result_time, result.timeText)
        binding.tvListening.text = "${result.listening} / 495"
        binding.tvReading.text = "${result.reading} / 495"
        binding.tvStatCorrect.text = "${getString(R.string.practice_stat_correct)}\n${result.correct}"
        binding.tvStatWrong.text = "${getString(R.string.practice_stat_wrong)}\n${result.wrong}"
        binding.tvStatSkip.text = "${getString(R.string.practice_stat_skip)}\n${result.skipped}"

        val partBindings = listOf(
            binding.resultPart1, binding.resultPart2, binding.resultPart3, binding.resultPart4,
            binding.resultPart5, binding.resultPart6, binding.resultPart7
        )
        exam.parts.forEachIndexed { index, part ->
            val (done, total) = result.partResults.getOrElse(index) { part.done to part.total }
            bindPart(partBindings[index], part, done, total)
        }

        binding.btnClose.setOnClickListener {
            startActivity(
                Intent(this, PracticeExamListActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
            finish()
        }
        binding.btnRetry.setOnClickListener {
            startActivity(
                Intent(this, PracticeExamOverviewActivity::class.java)
                    .putExtra(PracticeSampleData.EXTRA_EXAM_ID, exam.id)
            )
            finish()
        }
        binding.btnViewAnswers.setOnClickListener {
            startActivity(
                Intent(this, PracticeAnswerReviewActivity::class.java)
                    .putExtra(PracticeSampleData.EXTRA_EXAM_ID, exam.id)
                    .putExtra(PracticeSampleData.EXTRA_QUESTION_NUMBER, 11)
            )
        }
    }

    private fun bindPart(item: ItemPracticePartBinding, part: PracticePart, done: Int, total: Int) {
        item.tvPartIndex.text = part.number.toString()
        item.tvPartTitle.text = part.title
        item.tvPartProgress.text = getString(R.string.practice_part_progress, done, total)
        val colorId = resources.getIdentifier(part.colorResName, "color", packageName)
        item.tvPartIndex.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 12f * resources.displayMetrics.density
            setColor(getColor(if (colorId != 0) colorId else R.color.practice_primary))
        }
        item.progressFill.post {
            val parentWidth = (item.progressFill.parent as View).width
            val width = (parentWidth * done / total.coerceAtLeast(1).toFloat()).toInt()
            item.progressFill.layoutParams = item.progressFill.layoutParams.apply {
                this.width = width.coerceAtLeast(0)
            }
            item.progressFill.requestLayout()
        }
    }
}
