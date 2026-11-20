package com.example.auraenglish.practice.ui

import com.example.auraenglish.practice.utils.*

import com.example.auraenglish.practice.helper.*

import com.example.auraenglish.practice.data.*

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ActivityPracticePart5Binding
import com.example.auraenglish.databinding.ItemPracticeOptionBinding
import com.example.auraenglish.databinding.ItemPracticeQuestionNavBinding

// Màn làm Part 5
class PracticePart5Activity : AppCompatActivity() {

    private lateinit var binding: ActivityPracticePart5Binding
    private lateinit var exam: PracticeExam
    // Số câu hiện tại
    private var currentNumber = 12

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

        val examId = intent.getStringExtra(PracticeSampleData.EXTRA_EXAM_ID)
            ?: PracticeSampleData.currentExamId
        exam = PracticeSampleData.examById(examId)
        currentNumber = intent.getIntExtra(PracticeSampleData.EXTRA_QUESTION_NUMBER, 12)

        binding.tvExamTitle.text = getString(R.string.practice_exam_detail_title, exam.index)
        binding.tvTimer.text = "12:45"
        binding.progressBar.max = PracticeSampleData.part5Questions.size
        binding.progressBar.progressDrawable = getDrawable(R.drawable.bg_practice_progress_bar)

        // Bôi đen từ trong đề → popup lưu sổ từ vựng
        PracticeWordHelper.enableSelection(
            activity = this,
            textView = binding.tvQuestion,
            questionNumber = { currentNumber },
            onWordAdded = { refreshQuestionText() }
        )

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = confirmExit()
        })
        binding.btnClose.setOnClickListener { confirmExit() }
        binding.btnViewFlagged.setOnClickListener {
            startActivity(
                Intent(this, PracticeBookmarkedActivity::class.java)
                    .putExtra(PracticeSampleData.EXTRA_EXAM_ID, exam.id)
            )
        }
        binding.btnBookmark.setOnClickListener {
            val flagged = PracticeSampleData.toggleFlag(currentNumber)
            binding.btnBookmark.alpha = if (flagged) 1f else 0.4f
            refreshFlagBanner()
            renderQuestionNav()
        }
        binding.btnPrev.setOnClickListener { moveQuestion(-1) }
        binding.btnNext.setOnClickListener { moveQuestion(1) }

        showQuestion(currentNumber)
    }

    // Next vượt cuối list → hỏi nộp bài
    private fun moveQuestion(delta: Int) {
        val numbers = PracticeSampleData.part5Questions.map { it.number }
        val index = numbers.indexOf(currentNumber)
        val next = numbers.getOrNull(index + delta)
        if (next == null) {
            if (delta > 0) confirmSubmit()
            return
        }
        showQuestion(next)
    }

    private fun confirmExit() {
        PracticeConfirmHelper.showExitExam(this) { finish() }
    }

    private fun confirmSubmit() {
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

    private fun showQuestion(number: Int) {
        val question = PracticeSampleData.questionByNumber(number) ?: return
        currentNumber = number

        val list = PracticeSampleData.part5Questions
        val pos = list.indexOfFirst { it.number == number }.coerceAtLeast(0)
        binding.tvQuestionProgress.text = getString(R.string.practice_question_progress, pos + 1, list.size)
        binding.tvAnsweredCount.text = getString(
            R.string.practice_answered_count,
            list.count { it.isAnswered }
        )
        binding.progressBar.progress = list.count { it.isAnswered }
        binding.tvTag.text = question.tag
        refreshQuestionText()
        binding.btnBookmark.alpha = if (question.flagged) 1f else 0.4f

        bindOption(binding.optionA, 0, "A", question.options[0], question)
        bindOption(binding.optionB, 1, "B", question.options[1], question)
        bindOption(binding.optionC, 2, "C", question.options[2], question)
        bindOption(binding.optionD, 3, "D", question.options[3], question)

        refreshFlagBanner()
        renderQuestionNav()

        // Câu cuối: nút Next thành “Nộp bài”
        val isLast = pos == list.lastIndex
        binding.btnNext.text = getString(
            if (isLast) R.string.practice_submit_next else R.string.practice_next_question
        )
    }

    private fun refreshQuestionText() {
        val stem = PracticeSampleData.questionByNumber(currentNumber)?.stem.orEmpty()
        PracticeWordHelper.highlightSavedWords(binding.tvQuestion, stem)
    }

    private fun refreshFlagBanner() {
        val count = PracticeSampleData.part5Questions.count { it.flagged }
        binding.flagBanner.isVisible = count > 0
        binding.tvFlagBanner.text = getString(R.string.practice_flagged_banner, count)
    }

    private fun renderQuestionNav() {
        binding.questionNav.removeAllViews()
        PracticeSampleData.part5Questions.forEach { q ->
            val item = ItemPracticeQuestionNavBinding.inflate(
                LayoutInflater.from(this), binding.questionNav, false
            )
            item.tvNumber.text = q.number.toString()
            when {
                q.number == currentNumber -> {
                    item.tvNumber.setBackgroundResource(R.drawable.bg_practice_q_current)
                    item.tvNumber.setTextColor(getColor(R.color.white))
                }
                q.isAnswered -> {
                    item.tvNumber.setBackgroundResource(R.drawable.bg_practice_q_done)
                    item.tvNumber.setTextColor(getColor(R.color.practice_primary))
                }
                else -> {
                    item.tvNumber.setBackgroundResource(R.drawable.bg_practice_q_todo)
                    item.tvNumber.setTextColor(getColor(R.color.practice_text_secondary))
                }
            }
            item.flagDot.isVisible = q.flagged
            item.root.isClickable = true
            item.root.setOnClickListener { showQuestion(q.number) }
            binding.questionNav.addView(item.root)
        }
    }

    // Bind 1 option; click → setAnswer rồi re-render câu hiện tại.
    private fun bindOption(
        item: ItemPracticeOptionBinding,
        index: Int,
        letter: String,
        text: String,
        question: PracticeQuestion
    ) {
        item.tvLetter.text = letter
        item.tvOption.text = text
        val selected = question.userAnswerIndex == index
        item.root.setBackgroundResource(
            if (selected) R.drawable.bg_practice_option_selected else R.drawable.bg_practice_option
        )
        item.tvLetter.setBackgroundResource(
            if (selected) R.drawable.bg_practice_letter_selected else R.drawable.bg_practice_letter_default
        )
        item.tvLetter.setTextColor(
            getColor(if (selected) R.color.white else R.color.practice_text_secondary)
        )
        item.ivCheck.isVisible = selected
        item.tvOptionLabel.isVisible = false
        item.root.setOnClickListener {
            PracticeSampleData.setAnswer(question.number, index)
            showQuestion(currentNumber)
        }
    }
}
