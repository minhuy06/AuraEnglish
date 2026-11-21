package com.example.auraenglish.room.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.auraenglish.room.data.InviteResult
import com.example.auraenglish.room.data.RoomQuestionUiModel
import com.example.auraenglish.room.data.RoomSessionStore
import com.example.auraenglish.room.data.RoomSlotItem

data class RoomExamUiState(
    val currentIndex: Int,
    val totalCount: Int,
    val question: RoomQuestionUiModel,
    val selectedOptionIndex: Int? = null
) {
    val isFirst: Boolean get() = currentIndex == 0
    val isLast: Boolean get() = currentIndex == totalCount - 1
}

class RoomViewModel : ViewModel() {

    private val _lobbySlots = MutableLiveData<List<RoomSlotItem>>()
    val lobbySlots: LiveData<List<RoomSlotItem>> = _lobbySlots

    private val _memberCount = MutableLiveData<Int>()
    val memberCount: LiveData<Int> = _memberCount

    private val _isRoomFull = MutableLiveData<Boolean>()
    val isRoomFull: LiveData<Boolean> = _isRoomFull

    private val _isCurrentUserHost = MutableLiveData<Boolean>()
    val isCurrentUserHost: LiveData<Boolean> = _isCurrentUserHost

    private val _isCurrentUserReady = MutableLiveData<Boolean>()
    val isCurrentUserReady: LiveData<Boolean> = _isCurrentUserReady

    private val _examState = MutableLiveData<RoomExamUiState>()
    val examState: LiveData<RoomExamUiState> = _examState

    private var examQuestions: List<RoomQuestionUiModel> = emptyList()
    private var currentIndex: Int = 0

    init {
        refreshLobby()
    }

    fun refreshLobby() {
        _lobbySlots.value = RoomSessionStore.buildSlots()
        val count = RoomSessionStore.members.size
        _memberCount.value = count
        _isRoomFull.value = count >= RoomSessionStore.MAX_MEMBERS
        _isCurrentUserHost.value = RoomSessionStore.isCurrentUserHost()
        _isCurrentUserReady.value = RoomSessionStore.isCurrentUserReady()
    }

    fun toggleCurrentUserReady(): Boolean {
        val newReady = RoomSessionStore.toggleCurrentUserReady()
        refreshLobby()
        return newReady
    }

    fun canHostStartExam(): Boolean =
        RoomSessionStore.isCurrentUserHost() && RoomSessionStore.areAllNonHostMembersReady()

    fun inviteMember(rawName: String): InviteResult {
        val result = RoomSessionStore.inviteMember(rawName)
        if (result is InviteResult.Success) {
            refreshLobby()
        }
        return result
    }

    fun removeMember(memberId: String) {
        RoomSessionStore.removeMember(memberId)
        refreshLobby()
    }

    fun startExam() {
        RoomSessionStore.resetExamAnswers()
        examQuestions = RoomSessionStore.getQuestionsForCurrentRoom()
        currentIndex = 0
        emitExamState()
    }

    fun selectOption(optionIndex: Int) {
        val question = examQuestions.getOrNull(currentIndex) ?: return
        if (optionIndex !in question.options.indices) return
        RoomSessionStore.userAnswers[question.id] = optionIndex
        emitExamState()
    }

    fun goNext() {
        if (currentIndex < examQuestions.lastIndex) {
            currentIndex++
            emitExamState()
        }
    }

    fun goPrevious() {
        if (currentIndex > 0) {
            currentIndex--
            emitExamState()
        }
    }

    fun getUnansweredCount(): Int =
        examQuestions.count { !RoomSessionStore.userAnswers.containsKey(it.id) }

    private fun emitExamState() {
        val question = examQuestions.getOrNull(currentIndex) ?: return
        _examState.value = RoomExamUiState(
            currentIndex = currentIndex,
            totalCount = examQuestions.size,
            question = question,
            selectedOptionIndex = RoomSessionStore.userAnswers[question.id]
        )
    }
}
