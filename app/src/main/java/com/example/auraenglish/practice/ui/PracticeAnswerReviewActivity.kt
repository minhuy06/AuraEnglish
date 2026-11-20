package com.example.auraenglish.practice.ui

import com.example.auraenglish.practice.utils.*

import com.example.auraenglish.practice.helper.*

import com.example.auraenglish.practice.data.*

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ActivityPracticeAnswerReviewBinding
import com.example.auraenglish.databinding.ItemPracticeOptionBinding
import com.example.auraenglish.databinding.ItemPracticeQuestionNavBinding

class PracticeAnswerReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPracticeAnswerReviewBinding
    private var currentNumber = 11

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

        currentNumber = intent.getIntExtra(PracticeSampleData.EXTRA_QUESTION_NUMBER, 11)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnPrev.setOnClickListener { move(-1) }
        binding.btnNext.setOnClickListener { move(1) }

        showQuestion(currentNumber)
    }

    private fun move(delta: Int) {
        val numbers = PracticeSampleData.part5Questions.map { it.number }
        val index = numbers.indexOf(currentNumber)
        val next = numbers.getOrNull(index + delta) ?: return
        showQuestion(next)
    }

    private fun showQuestion(number: Int) {
        val question = PracticeSampleData.questionByNumber(number) ?: return
        currentNumber = number
        val list = PracticeSampleData.part5Questions
        val pos = list.indexOfFirst { it.number == number }.coerceAtLeast(0)
        binding.tvProgress.text = "${pos + 1} / ${list.size}"
        binding.tvTag.text = question.tag
        binding.tvQuestion.text = question.stem
        binding.tvTranslation.text = question.translation
        binding.tvExplanation.text = question.explanation

        val user = question.userAnswerIndex
        val correct = question.correctIndex
        val isWrong = user != null && user != correct
        binding.tvResultBadge.text = when {
            user == null -> "Chưa làm"
            isWrong -> getString(R.string.practice_wrong_label)
            else -> "Làm đúng"
        }
        binding.tvResultBadge.setTextColor(
            getColor(
                when {
                    user == null -> R.color.practice_pending_text
                    isWrong -> R.color.practice_wrong
                    else -> R.color.practice_correct
                }
            )
        )

        bindOption(binding.optionA, 0, "A", question.options[0], question)
        bindOption(binding.optionB, 1, "B", question.options[1], question)
        bindOption(binding.optionC, 2, "C", question.options[2], question)
        bindOption(binding.optionD, 3, "D", question.options[3], question)
        renderNav()
    }

    private fun renderNav() {
        binding.questionNav.removeAllViews()
        PracticeSampleData.part5Questions.forEach { q ->
            val item = ItemPracticeQuestionNavBinding.inflate(
                LayoutInflater.from(this), binding.questionNav, false
            )
            item.tvNumber.text = q.number.toString()
            val user = q.userAnswerIndex
            val bg = when {
                q.number == currentNumber -> R.drawable.bg_practice_q_current
                user == null -> R.drawable.bg_practice_q_todo
                user == q.correctIndex -> R.drawable.bg_practice_q_correct
                else -> R.drawable.bg_practice_q_wrong
            }
            item.tvNumber.setBackgroundResource(bg)
            item.tvNumber.setTextColor(
                getColor(
                    if (bg == R.drawable.bg_practice_q_todo) R.color.practice_text_secondary
                    else R.color.white
                )
            )
            item.flagDot.isVisible = q.flagged
            item.root.isClickable = true
            item.root.setOnClickListener { showQuestion(q.number) }
            binding.questionNav.addView(item.root)
        }
    }

    private fun bindOption(
        item: ItemPracticeOptionBinding,
        index: Int,
        letter: String,
        text: String,
        question: PracticeQuestion
    ) {
        item.tvLetter.text = letter
        item.tvOption.text = text
        item.tvOptionLabel.isVisible = false
        item.ivCheck.isVisible = false
        item.root.setBackgroundResource(R.drawable.bg_practice_option)
        item.tvLetter.setBackgroundResource(R.drawable.bg_practice_letter_default)
        item.tvLetter.setTextColor(getColor(R.color.practice_text_secondary))

        when {
            index == question.correctIndex -> {
                item.root.setBackgroundResource(R.drawable.bg_practice_option_correct)
                item.tvLetter.setBackgroundResource(R.drawable.bg_practice_letter_selected)
                item.tvLetter.setTextColor(getColor(R.color.white))
                item.tvOptionLabel.isVisible = true
                item.tvOptionLabel.text = getString(R.string.practice_correct_label)
                item.tvOptionLabel.setTextColor(getColor(R.color.practice_correct))
                item.ivCheck.isVisible = true
                item.ivCheck.setColorFilter(getColor(R.color.practice_correct))
            }
            question.userAnswerIndex == index && index != question.correctIndex -> {
                item.root.setBackgroundResource(R.drawable.bg_practice_option_wrong)
                item.tvOptionLabel.isVisible = true
                item.tvOptionLabel.text = getString(R.string.practice_your_choice)
                item.tvOptionLabel.setTextColor(getColor(R.color.practice_wrong))
            }
        }
    }
}
