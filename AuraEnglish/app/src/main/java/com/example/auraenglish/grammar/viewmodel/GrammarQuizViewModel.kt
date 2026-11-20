package com.example.auraenglish.grammar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.auraenglish.grammar.data.GrammarQuestionUiModel
import com.example.auraenglish.grammar.data.GrammarSessionStore

data class GrammarQuizUiState(
    val currentIndex: Int = 0,
    val totalCount: Int = GrammarSessionStore.questions.size,
    val question: GrammarQuestionUiModel = GrammarSessionStore.questions.first(),
    val selectedOptionIndex: Int? = null
) {
    val isFirst: Boolean get() = currentIndex == 0
    val isLast: Boolean get() = currentIndex == totalCount - 1
}

class GrammarQuizViewModel : ViewModel() {

    private val questions = GrammarSessionStore.questions

    private val _quizState = MutableLiveData<GrammarQuizUiState>()
    val quizState: LiveData<GrammarQuizUiState> = _quizState

    private var currentIndex: Int = 0

    init {
        emitState()
    }

    fun selectOption(optionIndex: Int) {
        val questionId = questions[currentIndex].id
        GrammarSessionStore.userAnswers[questionId] = optionIndex
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

    fun getUnansweredCount(): Int =
        questions.count { !GrammarSessionStore.userAnswers.containsKey(it.id) }

    private fun emitState() {
        val q = questions[currentIndex]
        _quizState.value = GrammarQuizUiState(
            currentIndex = currentIndex,
            totalCount = questions.size,
            question = q,
            selectedOptionIndex = GrammarSessionStore.userAnswers[q.id]
        )
    }
}
