package com.example.auraenglish.goal.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.auraenglish.R
import com.example.auraenglish.auth.utils.applyAuthInsets
import com.example.auraenglish.databinding.ActivityAssessmentQuizBinding
import com.example.auraenglish.goal.data.AssessmentSessionStore
import com.example.auraenglish.goal.data.QuestionType
import com.example.auraenglish.goal.helper.GoalDialogHelper
import com.example.auraenglish.goal.viewmodel.AssessmentQuizUiState
import com.example.auraenglish.goal.viewmodel.AssessmentQuizViewModel
import com.example.auraenglish.practice.utils.setOnClickDebounced

class AssessmentQuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAssessmentQuizBinding
    private val viewModel: AssessmentQuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAssessmentQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyAuthInsets(binding.rootAssessmentQuiz)

        observeViewModel()
        setupListeners()
        setupBackHandler()
    }

    private fun setupBackHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitAssessmentDialog()
            }
        })
    }

    private fun showExitAssessmentDialog() {
        GoalDialogHelper.showExitAssessment(this) {
            AssessmentSessionStore.resetSession()
            finish()
        }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            bindQuizState(state)
        }
    }

    private fun bindQuizState(state: AssessmentQuizUiState) = with(binding) {
        val q = state.question

        // Timer
        tvQuizTimer.text = state.formattedTime

        // Progress
        tvQuizProgressNumber.text = getString(R.string.goal_quiz_question_format, state.progress, state.totalCount)
        pbQuizProgress.max = state.totalCount
        pbQuizProgress.progress = state.progress

        // Category Tag
        tvQuizCategoryTag.text = when (q.type) {
            QuestionType.GRAMMAR -> "Ngữ pháp"
            QuestionType.VOCAB -> "Từ vựng"
            QuestionType.READING -> "Đọc hiểu"
            QuestionType.LISTENING -> "Nghe"
        }

        // Reading passage
        if (!q.passage.isNullOrBlank()) {
            layoutReadingPassage.visibility = View.VISIBLE
            tvReadingPassage.text = q.passage
        } else {
            layoutReadingPassage.visibility = View.GONE
        }

        // Question prompt
        tvQuizQuestionPrompt.text = q.question

        // Options
        val options = listOf(
            Triple(optionA, tvOptionALetter, tvOptionAText),
            Triple(optionB, tvOptionBLetter, tvOptionBText),
            Triple(optionC, tvOptionCLetter, tvOptionCText),
            Triple(optionD, tvOptionDLetter, tvOptionDText)
        )

        options.forEachIndexed { index, (container, letterView, textView) ->
            textView.text = q.options.getOrElse(index) { "" }
            val isSelected = (state.selectedOptionIndex == index)
            styleOption(container, letterView, textView, isSelected)
        }

        // Next / Submit button text
        btnQuizNextOrSubmit.setText(
            if (state.isLast) R.string.goal_btn_submit
            else R.string.goal_btn_next_question
        )

        // Previous link
        tvQuizPrevQuestion.isEnabled = !state.isFirst
        tvQuizPrevQuestion.alpha = if (state.isFirst) 0.4f else 1f
    }

    private fun styleOption(
        container: LinearLayout,
        letterView: TextView,
        textView: TextView,
        isSelected: Boolean
    ) {
        container.setBackgroundResource(
            if (isSelected) R.drawable.bg_goal_option_selected
            else R.drawable.bg_goal_card
        )

        letterView.backgroundTintList = ContextCompat.getColorStateList(
            this,
            if (isSelected) R.color.goal_primary else R.color.goal_quiz_option_bg
        )
        letterView.setTextColor(
            ContextCompat.getColor(
                this,
                if (isSelected) R.color.white else R.color.goal_text_primary
            )
        )
        textView.setTextColor(
            ContextCompat.getColor(
                this,
                if (isSelected) R.color.goal_primary_dark else R.color.goal_text_primary
            )
        )
        textView.typeface = ResourcesCompat.getFont(
            this,
            if (isSelected) R.font.be_vietnam_pro_bold else R.font.be_vietnam_pro_medium
        )
    }

    private fun setupListeners() = with(binding) {
        btnQuizBack.setOnClickListener {
            showExitAssessmentDialog()
        }

        optionA.setOnClickListener { viewModel.selectOption(0) }
        optionB.setOnClickListener { viewModel.selectOption(1) }
        optionC.setOnClickListener { viewModel.selectOption(2) }
        optionD.setOnClickListener { viewModel.selectOption(3) }

        tvQuizPrevQuestion.setOnClickListener {
            viewModel.goPrevious()
        }

        btnQuizNextOrSubmit.setOnClickDebounced(300L) {
            val state = viewModel.uiState.value ?: return@setOnClickDebounced
            if (state.isLast) {
                GoalDialogHelper.showSubmitAssessment(
                    activity = this@AssessmentQuizActivity,
                    answeredCount = viewModel.getAnsweredCount(),
                    skippedCount = viewModel.getSkippedCount()
                ) {
                    startActivity(Intent(this@AssessmentQuizActivity, AssessmentResultActivity::class.java))
                    finish()
                }
            } else {
                viewModel.goNext()
            }
        }
    }
}
