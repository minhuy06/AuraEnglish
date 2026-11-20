package com.example.auraenglish.room.ui

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ActivityRoomExamBinding
import com.example.auraenglish.practice.helper.PracticeConfirmHelper
import com.example.auraenglish.practice.utils.setOnClickDebounced
import com.example.auraenglish.practice.utils.showToast
import com.example.auraenglish.room.data.RoomSessionStore
import com.example.auraenglish.room.viewmodel.RoomExamUiState
import com.example.auraenglish.room.viewmodel.RoomViewModel
import java.util.Locale

class RoomExamActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoomExamBinding
    private val viewModel: RoomViewModel by viewModels()

    private val totalDurationMillis: Long by lazy {
        RoomSessionStore.currentRoom.durationMinutes.coerceAtLeast(1) * 60_000L
    }
    private var remainingMillis: Long = 0L
    private var countDownTimer: CountDownTimer? = null
    private var hasSubmitted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRoomExamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null || viewModel.examState.value == null) {
            viewModel.startExam()
        }
        remainingMillis = savedInstanceState?.getLong(KEY_REMAINING_MILLIS, totalDurationMillis)
            ?: totalDurationMillis

        setupViews()
        observeViewModel()
        setupListeners()
        startTimer(remainingMillis)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(KEY_REMAINING_MILLIS, remainingMillis)
    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        countDownTimer = null
        super.onDestroy()
    }

    private fun setupViews() = with(binding) {
        val basePadding = resources.getDimensionPixelSize(R.dimen.room_screen_padding)
        ViewCompat.setOnApplyWindowInsetsListener(rootRoomExam) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                basePadding + bars.left,
                basePadding + bars.top,
                basePadding + bars.right,
                basePadding + bars.bottom
            )
            insets
        }

        val room = RoomSessionStore.currentRoom
        tvRoomExamHeader.text = getString(R.string.room_exam_header_fmt, room.code, room.topic.label)
        tvRoomExamLiveStatus.text = getString(
            R.string.room_exam_live_status_fmt,
            RoomSessionStore.members.size
        )
        tvRoomExamTimer.text = formatTimer(remainingMillis)
    }

    private fun observeViewModel() {
        viewModel.examState.observe(this) { state ->
            bindExamState(state)
        }
    }

    private fun bindExamState(state: RoomExamUiState) = with(binding) {
        val currentNumber = state.currentIndex + 1
        pbRoomExam.progress = if (state.totalCount > 0) {
            (currentNumber * 100) / state.totalCount
        } else {
            0
        }
        tvRoomExamCounter.text = getString(
            R.string.room_exam_progress_fmt,
            currentNumber,
            state.totalCount
        )

        val question = state.question
        tvRoomQuestionLabel.text = getString(R.string.grammar_question_label_format, question.number)
        tvRoomQuestionText.text = question.question

        val optionViews = listOf(
            Triple(roomOptA, tvRoomOptALetter, tvRoomOptAText),
            Triple(roomOptB, tvRoomOptBLetter, tvRoomOptBText),
            Triple(roomOptC, tvRoomOptCLetter, tvRoomOptCText),
            Triple(roomOptD, tvRoomOptDLetter, tvRoomOptDText)
        )

        optionViews.forEachIndexed { index, (container, letter, textView) ->
            textView.text = question.options.getOrElse(index) { "" }
            styleOption(container, letter, isSelected = state.selectedOptionIndex == index)
        }

        btnRoomNextOrSubmit.setText(
            if (state.isLast) R.string.grammar_btn_submit else R.string.grammar_btn_next
        )
        btnRoomPrevQuestion.isEnabled = !state.isFirst
        btnRoomPrevQuestion.alpha = if (state.isFirst) 0.5f else 1f
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
                if (isSelected) R.color.white else R.color.room_text_primary
            )
        )
    }

    private fun setupListeners() = with(binding) {
        btnExitRoomExam.setOnClickDebounced {
            confirmExitExam()
        }

        onBackPressedDispatcher.addCallback(this@RoomExamActivity) {
            confirmExitExam()
        }

        roomOptA.setOnClickListener { viewModel.selectOption(0) }
        roomOptB.setOnClickListener { viewModel.selectOption(1) }
        roomOptC.setOnClickListener { viewModel.selectOption(2) }
        roomOptD.setOnClickListener { viewModel.selectOption(3) }

        btnRoomPrevQuestion.setOnClickListener {
            viewModel.goPrevious()
            scrollRoomExam.smoothScrollTo(0, 0)
        }

        btnRoomNextOrSubmit.setOnClickDebounced(350L) {
            val state = viewModel.examState.value ?: return@setOnClickDebounced
            if (state.isLast) {
                PracticeConfirmHelper.showSubmitExam(
                    activity = this@RoomExamActivity,
                    unansweredCount = viewModel.getUnansweredCount()
                ) {
                    submitExam()
                }
            } else {
                viewModel.goNext()
                scrollRoomExam.smoothScrollTo(0, 0)
            }
        }
    }

    private fun confirmExitExam() {
        PracticeConfirmHelper.showExitExam(this) {
            finish()
        }
    }

    private fun startTimer(durationMillis: Long) {
        countDownTimer?.cancel()
        if (durationMillis <= 0L) {
            remainingMillis = 0L
            binding.tvRoomExamTimer.text = formatTimer(0L)
            showToast(R.string.room_exam_time_up)
            submitExam()
            return
        }

        countDownTimer = object : CountDownTimer(durationMillis, TIMER_INTERVAL_MS) {
            override fun onTick(millisUntilFinished: Long) {
                remainingMillis = millisUntilFinished
                binding.tvRoomExamTimer.text = formatTimer(millisUntilFinished)
            }

            override fun onFinish() {
                remainingMillis = 0L
                binding.tvRoomExamTimer.text = formatTimer(0L)
                if (!hasSubmitted && !isFinishing && !isDestroyed) {
                    showToast(R.string.room_exam_time_up)
                    submitExam()
                }
            }
        }.start()
    }

    private fun submitExam() {
        if (hasSubmitted) return
        hasSubmitted = true
        countDownTimer?.cancel()
        countDownTimer = null

        val elapsedSeconds = ((totalDurationMillis - remainingMillis) / 1000L)
            .toInt()
            .coerceAtLeast(1)
        RoomSessionStore.finishExamAndBuildRankings(elapsedSeconds)
        startActivity(Intent(this, RoomRankingActivity::class.java))
        finish()
    }

    private fun formatTimer(millis: Long): String {
        val totalSeconds = (millis / 1000L).coerceAtLeast(0L)
        val minutes = totalSeconds / 60L
        val seconds = totalSeconds % 60L
        return String.format(Locale.US, "⏱ %02d:%02d", minutes, seconds)
    }

    companion object {
        private const val KEY_REMAINING_MILLIS = "room_exam_remaining_millis"
        private const val TIMER_INTERVAL_MS = 1000L
    }
}
