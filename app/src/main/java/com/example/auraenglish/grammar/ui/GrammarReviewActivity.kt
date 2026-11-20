package com.example.auraenglish.grammar.ui

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ActivityGrammarReviewBinding
import com.example.auraenglish.grammar.adapter.GrammarQuestionNavAdapter
import com.example.auraenglish.grammar.data.GrammarNavItemUiModel
import com.example.auraenglish.grammar.data.GrammarSessionStore

class GrammarReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGrammarReviewBinding
    private lateinit var navAdapter: GrammarQuestionNavAdapter
    private val questions = GrammarSessionStore.questions
    private var currentIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGrammarReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentIndex = savedInstanceState?.getInt(KEY_REVIEW_INDEX, 0) ?: 0

        setupViews()
        bindCurrentReview()
        setupListeners()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_REVIEW_INDEX, currentIndex)
    }

    private fun setupViews() = with(binding) {
        ViewCompat.setOnApplyWindowInsetsListener(rootGrammarReview) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        tvReviewSubtitle.text = getString(
            R.string.grammar_review_subtitle_format,
            GrammarSessionStore.EXAM_TITLE
        )

        navAdapter = GrammarQuestionNavAdapter { index ->
            currentIndex = index
            bindCurrentReview()
        }

        rvQuestionNav.apply {
            layoutManager = LinearLayoutManager(
                this@GrammarReviewActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            setHasFixedSize(true)
            adapter = navAdapter
        }
    }

    private fun bindCurrentReview() = with(binding) {
        val total = questions.size
        val q = questions[currentIndex]
        val userChoice = GrammarSessionStore.userAnswers[q.id]
        val correctCount = questions.count { GrammarSessionStore.userAnswers[it.id] == it.correctIndex }

        tvReviewCounter.text = getString(R.string.grammar_review_counter_format, correctCount, total)
        pbReviewProgress.progress = ((correctCount * 100f) / total.coerceAtLeast(1)).toInt()

        val navItems = questions.mapIndexed { idx, item ->
            GrammarNavItemUiModel(
                questionIndex = idx,
                number = item.number,
                isCorrect = GrammarSessionStore.userAnswers[item.id] == item.correctIndex,
                isSelected = idx == currentIndex
            )
        }
        navAdapter.submitList(navItems)
        rvQuestionNav.smoothScrollToPosition(currentIndex)

        tvReviewQuestionHeader.text = getString(
            R.string.grammar_review_question_header_format,
            q.number,
            total
        )
        tvReviewQuestionText.text = q.question
        tvExplanationRule.text = q.explanationRule
        tvExplanationExample.text = q.explanationExample

        val rows = listOf(
            OptionRow(reviewOptionA, tvRevALetter, tvRevAText, tvRevAMark),
            OptionRow(reviewOptionB, tvRevBLetter, tvRevBText, tvRevBMark),
            OptionRow(reviewOptionC, tvRevCLetter, tvRevCText, tvRevCMark),
            OptionRow(reviewOptionD, tvRevDLetter, tvRevDText, tvRevDMark)
        )

        rows.forEachIndexed { index, row ->
            row.text.text = q.options.getOrElse(index) { "" }
            val isCorrectOption = index == q.correctIndex
            val isWrongSelection = userChoice == index && index != q.correctIndex

            when {
                isCorrectOption -> {
                    row.container.setBackgroundResource(R.drawable.bg_grammar_option_correct)
                    row.letter.setBackgroundResource(R.drawable.bg_grammar_circle_correct)
                    row.letter.setTextColor(ContextCompat.getColor(this@GrammarReviewActivity, R.color.white))
                    row.mark.text = "✓"
                    row.mark.setTextColor(ContextCompat.getColor(this@GrammarReviewActivity, R.color.grammar_correct_text))
                }
                isWrongSelection -> {
                    row.container.setBackgroundResource(R.drawable.bg_grammar_option_wrong)
                    row.letter.setBackgroundResource(R.drawable.bg_grammar_circle_wrong)
                    row.letter.setTextColor(ContextCompat.getColor(this@GrammarReviewActivity, R.color.white))
                    row.mark.text = "✗"
                    row.mark.setTextColor(ContextCompat.getColor(this@GrammarReviewActivity, R.color.grammar_wrong_text))
                }
                else -> {
                    row.container.setBackgroundResource(R.drawable.bg_grammar_option_default)
                    row.letter.setBackgroundResource(R.drawable.bg_grammar_circle_default)
                    row.letter.setTextColor(ContextCompat.getColor(this@GrammarReviewActivity, R.color.grammar_text_primary))
                    row.mark.text = ""
                }
            }
        }

        btnNextReviewQuestion.setText(
            if (currentIndex == questions.lastIndex) R.string.grammar_btn_finish_review
            else R.string.grammar_btn_next_review
        )
    }

    private fun setupListeners() = with(binding) {
        btnBackGrammarReview.setOnClickListener { finish() }
        btnNextReviewQuestion.setOnClickListener {
            if (currentIndex < questions.lastIndex) {
                currentIndex++
                bindCurrentReview()
            } else {
                finish()
            }
        }
    }

    private data class OptionRow(
        val container: LinearLayout,
        val letter: TextView,
        val text: TextView,
        val mark: TextView
    )

    companion object {
        private const val KEY_REVIEW_INDEX = "key_grammar_review_index"
    }
}
