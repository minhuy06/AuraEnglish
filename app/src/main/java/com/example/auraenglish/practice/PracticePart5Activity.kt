package com.example.auraenglish.practice

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ActivityPracticePart5Binding
import com.example.auraenglish.databinding.ItemPracticeOptionBinding

class PracticePart5Activity : AppCompatActivity() {

    private lateinit var binding: ActivityPracticePart5Binding
    private var selected = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPracticePart5Binding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        binding.tvExamTitle.text = getString(R.string.practice_exam_detail_title, "01")
        binding.tvQuestionProgress.text = getString(R.string.practice_question_progress, 12, 30)
        binding.tvAnsweredCount.text = getString(R.string.practice_answered_count, 11)
        binding.tvTimer.text = "12:45"
        binding.tvFlagBanner.text = getString(R.string.practice_flagged_banner, 3)
        binding.tvQuestion.text = "The marketing team will ______ the new product to our clients next Monday."

        setupQuestionNav()
        bindOption(binding.optionA, "A", "present", true)
        bindOption(binding.optionB, "B", "presents", false)
        bindOption(binding.optionC, "C", "presenting", false)
        bindOption(binding.optionD, "D", "presented", false)

        binding.btnClose.setOnClickListener { finish() }
        binding.btnViewFlagged.setOnClickListener {
            startActivity(Intent(this, PracticeBookmarkedActivity::class.java))
        }
        binding.tvQuestion.setOnLongClickListener {
            binding.wordMenu.isVisible = !binding.wordMenu.isVisible
            true
        }
        binding.btnAddWord.setOnClickListener {
            binding.wordMenu.isVisible = false
            Toast.makeText(this, R.string.practice_add_word, Toast.LENGTH_SHORT).show()
        }
        binding.btnCopy.setOnClickListener {
            binding.wordMenu.isVisible = false
            Toast.makeText(this, R.string.practice_copy, Toast.LENGTH_SHORT).show()
        }
        binding.btnNext.setOnClickListener {
            startActivity(Intent(this, PracticeResultActivity::class.java))
        }
    }

    private fun setupQuestionNav() {
        val numbers = listOf(9, 10, 11, 12, 13, 14, 15)
        val flagged = setOf(11, 12, 14)
        numbers.forEach { num ->
            val tv = TextView(this).apply {
                layoutParams = android.widget.LinearLayout.LayoutParams(dp(36), dp(36)).also {
                    it.marginEnd = dp(8)
                }
                gravity = Gravity.CENTER
                text = num.toString()
                textSize = 13f
                setTypeface(typeface, android.graphics.Typeface.BOLD)
                when {
                    num == 12 -> {
                        setBackgroundResource(R.drawable.bg_practice_q_current)
                        setTextColor(getColor(R.color.white))
                    }
                    num < 12 -> {
                        setBackgroundResource(R.drawable.bg_practice_q_done)
                        setTextColor(getColor(R.color.practice_primary))
                    }
                    else -> {
                        setBackgroundResource(R.drawable.bg_practice_q_todo)
                        setTextColor(getColor(R.color.practice_text_secondary))
                    }
                }
                if (num in flagged) {
                    compoundDrawablePadding = 0
                }
            }
            binding.questionNav.addView(tv)
        }
    }

    private fun bindOption(item: ItemPracticeOptionBinding, letter: String, text: String, isSelected: Boolean) {
        item.tvLetter.text = letter
        item.tvOption.text = text
        if (isSelected) {
            selected = letter[0] - 'A'
            item.root.setBackgroundResource(R.drawable.bg_practice_option_selected)
            item.tvLetter.setBackgroundResource(R.drawable.bg_practice_letter_selected)
            item.tvLetter.setTextColor(getColor(R.color.white))
        }
        item.root.setOnClickListener {
            listOf(binding.optionA, binding.optionB, binding.optionC, binding.optionD).forEach {
                it.root.setBackgroundResource(R.drawable.bg_practice_option)
                it.tvLetter.setBackgroundResource(R.drawable.bg_practice_letter_default)
                it.tvLetter.setTextColor(getColor(R.color.practice_text_secondary))
            }
            item.root.setBackgroundResource(R.drawable.bg_practice_option_selected)
            item.tvLetter.setBackgroundResource(R.drawable.bg_practice_letter_selected)
            item.tvLetter.setTextColor(getColor(R.color.white))
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
