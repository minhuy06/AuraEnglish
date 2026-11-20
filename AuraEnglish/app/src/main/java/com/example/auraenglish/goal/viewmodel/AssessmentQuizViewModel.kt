package com.example.auraenglish.goal.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.auraenglish.goal.data.AssessmentQuestion
import com.example.auraenglish.goal.data.AssessmentSessionStore

data class AssessmentQuizUiState(
    val currentIndex: Int = 0,
    val totalCount: Int = AssessmentSessionStore.questions.size,
    val question: AssessmentQuestion = AssessmentSessionStore.questions.first(),
    val selectedOptionIndex: Int? = null,
    val formattedTime: String = "15:00"
) {
    val isFirst: Boolean get() = currentIndex == 0
    val isLast: Boolean get() = currentIndex == totalCount - 1
    val progress: Int get() = currentIndex + 1
}

class AssessmentQuizViewModel : ViewModel() {

    private val questions = AssessmentSessionStore.questions

    private val _uiState = MutableLiveData<AssessmentQuizUiState>()
    val uiState: LiveData<AssessmentQuizUiState> = _uiState

    private var currentIndex: Int = 6 // start at question 7 as in mockup, or 0 if fresh
    private var remainingSeconds: Long = 13 * 60 + 45 // 13:45 as in mockup
    private var timer: CountDownTimer? = null

    init {
        startTimer()
        emitState()
    }

    private fun startTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(remainingSeconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingSeconds = millisUntilFinished / 1000
                val mins = remainingSeconds / 60
                val secs = remainingSeconds % 60
                val formatted = String.format("%02d:%02d", mins, secs)
                _uiState.value = _uiState.value?.copy(formattedTime = formatted)
            }

            override fun onFinish() {
                _uiState.value = _uiState.value?.copy(formattedTime = "00:00")
            }
        }.start()
    }

    fun selectOption(optionIndex: Int) {
        val q = questions[currentIndex]
        AssessmentSessionStore.userAnswers[q.id] = optionIndex
        emitState()
    }

    fun goNext() {
        if (currentIndex < questions.lastIndex) {
            currentIndex++
            emitState()
        }
    }

    fun goPrevious() {
        if (currentIndex > 0) {
            currentIndex--
            emitState()
        }
    }

    fun jumpToQuestion(index: Int) {
        if (index in questions.indices) {
            currentIndex = index
            emitState()
        }
    }

    fun getAnsweredCount(): Int =
        questions.count { AssessmentSessionStore.userAnswers.containsKey(it.id) }

    fun getSkippedCount(): Int =
        questions.count { !AssessmentSessionStore.userAnswers.containsKey(it.id) }

    private fun emitState() {
        val q = questions[currentIndex]
        val mins = remainingSeconds / 60
        val secs = remainingSeconds % 60
        _uiState.value = AssessmentQuizUiState(
            currentIndex = currentIndex,
            totalCount = questions.size,
            question = q,
            selectedOptionIndex = AssessmentSessionStore.userAnswers[q.id],
            formattedTime = String.format("%02d:%02d", mins, secs)
        )
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }
}
