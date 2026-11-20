package com.example.auraenglish.grammar.ui

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ActivityGrammarQuizBinding
import com.example.auraenglish.grammar.data.GrammarSessionStore
import com.example.auraenglish.grammar.viewmodel.GrammarQuizUiState
import com.example.auraenglish.grammar.viewmodel.GrammarQuizViewModel
import com.example.auraenglish.practice.helper.PracticeConfirmHelper
import com.example.auraenglish.practice.utils.setOnClickDebounced

class GrammarQuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGrammarQuizBinding
    private val viewModel: GrammarQuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (savedInstanceState == null) {
            GrammarSessionStore.resetSession()
        }
        binding = ActivityGrammarQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeViewModel()
        setupListeners()
    }

    private fun setupViews() = with(binding) {
        ViewCompat.setOnApplyWindowInsetsListener(rootGrammarQuiz) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }

    private fun observeViewModel() {
        viewModel.quizState.observe(this) { state ->
            bindQuizState(state)
        }
    }

    private fun bindQuizState(state: GrammarQuizUiState) = with(binding) {
        val q = state.question
        tvQuizSubtitle.text = getString(
            R.string.grammar_quiz_subtitle_format,
            GrammarSessionStore.EXAM_TITLE,
            q.number,
            state.totalCount
        )
        tvQuestionLabel.text = getString(R.string.grammar_question_label_format, q.number)
        tvQuestionText.text = q.question

        val optionViews = listOf(
            Triple(optionA, tvOptionALetter, tvOptionAText),
            Triple(optionB, tvOptionBLetter, tvOptionBText),
            Triple(optionC, tvOptionCLetter, tvOptionCText),
            Triple(optionD, tvOptionDLetter, tvOptionDText)
        )

        optionViews.forEachIndexed { index, (container, letter, textView) ->
            textView.text = q.options.getOrElse(index) { "" }
            styleOption(container, letter, isSelected = state.selectedOptionIndex == index)
        }

        btnNextOrSubmit.setText(
            if (state.isLast) R.string.grammar_btn_submit else R.string.grammar_btn_next
        )
        btnPrevQuestion.isEnabled = !state.isFirst
        btnPrevQuestion.alpha = if (state.isFirst) 0.5f else 1f
    }

    private fun styleOption(container: LinearLayout, letter: TextView, isSelected: Boolean) {
        container.setBackgroundResource(
            if (isSelected) R.drawable.bg_grammar_option_selected
            else R.drawable.bg_grammar_option_default
        )
        letter.setBackgroundResource(
            if (isSelected) R.drawable.bg_grammar_circle_selected
            else R.drawable.bg_grammar_circle_default
        )
        letter.setTextColor(
            ContextCompat.getColor(
                this,
                if (isSelected) R.color.white else R.color.grammar_text_primary
            )
        )
    }

    private fun setupListeners() = with(binding) {
        btnBackGrammarQuiz.setOnClickListener {
            PracticeConfirmHelper.showExitExam(this@GrammarQuizActivity) { finish() }
        }

        optionA.setOnClickListener { viewModel.selectOption(0) }
        optionB.setOnClickListener { viewModel.selectOption(1) }
        optionC.setOnClickListener { viewModel.selectOption(2) }
        optionD.setOnClickListener { viewModel.selectOption(3) }

        btnPrevQuestion.setOnClickListener { viewModel.goPrevious() }

        btnNextOrSubmit.setOnClickDebounced(350L) {
            val state = viewModel.quizState.value ?: return@setOnClickDebounced
            if (state.isLast) {
                PracticeConfirmHelper.showSubmitExam(
                    activity = this@GrammarQuizActivity,
                    unansweredCount = viewModel.getUnansweredCount()
                ) {
                    startActivity(Intent(this@GrammarQuizActivity, GrammarReviewActivity::class.java))
                    finish()
                }
            } else {
                viewModel.goNext()
            }
        }
    }
}
