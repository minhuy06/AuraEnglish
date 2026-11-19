package com.example.auraenglish.practice

import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ActivityPracticeAnswerReviewBinding
import com.example.auraenglish.databinding.ItemPracticeOptionBinding

class PracticeAnswerReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPracticeAnswerReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPracticeAnswerReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        binding.tvProgress.text = "10 / 200"
        binding.tvQuestion.text = "The new policy has been ______ by all department managers."
        binding.tvTranslation.text = "Chính sách mới đã được tất cả trưởng phòng phê duyệt."
        binding.tvExplanation.text =
            "Cấu trúc bị động hiện tại hoàn thành: has/have been + V3 (approved). \"Approving\" là dạng V-ing nên không đúng."

        setupQuestionNav()
        bindOption(binding.optionA, "A", "approved", OptionState.CORRECT)
        bindOption(binding.optionB, "B", "approving", OptionState.WRONG)
        bindOption(binding.optionC, "C", "approve", OptionState.NORMAL)
        bindOption(binding.optionD, "D", "approval", OptionState.NORMAL)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnPrev.setOnClickListener { finish() }
        binding.btnNext.setOnClickListener { finish() }
    }

    private enum class OptionState { NORMAL, CORRECT, WRONG }

    private fun setupQuestionNav() {
        val states = mapOf(
            9 to R.drawable.bg_practice_q_correct,
            10 to R.drawable.bg_practice_q_current,
            11 to R.drawable.bg_practice_q_correct,
            12 to R.drawable.bg_practice_q_wrong,
            13 to R.drawable.bg_practice_q_wrong,
            14 to R.drawable.bg_practice_q_correct,
            15 to R.drawable.bg_practice_q_todo
        )
        states.forEach { (num, bg) ->
            val tv = TextView(this).apply {
                layoutParams = android.widget.LinearLayout.LayoutParams(dp(36), dp(36)).also {
                    it.marginEnd = dp(8)
                }
                gravity = Gravity.CENTER
                text = num.toString()
                textSize = 13f
                setTypeface(typeface, android.graphics.Typeface.BOLD)
                setBackgroundResource(bg)
                setTextColor(
                    getColor(
                        if (bg == R.drawable.bg_practice_q_todo) R.color.practice_text_secondary
                        else R.color.white
                    )
                )
            }
            binding.questionNav.addView(tv)
        }
    }

    private fun bindOption(item: ItemPracticeOptionBinding, letter: String, text: String, state: OptionState) {
        item.tvLetter.text = letter
        item.tvOption.text = text
        when (state) {
            OptionState.CORRECT -> {
                item.root.setBackgroundResource(R.drawable.bg_practice_option_correct)
                item.tvLetter.setBackgroundResource(R.drawable.bg_practice_letter_selected)
                item.tvLetter.setTextColor(getColor(R.color.white))
                item.tvOptionLabel.isVisible = true
                item.tvOptionLabel.text = getString(R.string.practice_correct_label)
                item.tvOptionLabel.setTextColor(getColor(R.color.practice_correct))
            }
            OptionState.WRONG -> {
                item.root.setBackgroundResource(R.drawable.bg_practice_option_wrong)
                item.tvLetter.setBackgroundResource(R.drawable.bg_practice_letter_default)
                item.tvOptionLabel.isVisible = true
                item.tvOptionLabel.text = getString(R.string.practice_your_choice)
                item.tvOptionLabel.setTextColor(getColor(R.color.practice_wrong))
            }
            OptionState.NORMAL -> Unit
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
