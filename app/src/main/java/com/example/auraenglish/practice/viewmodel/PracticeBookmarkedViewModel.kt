package com.example.auraenglish.practice.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.auraenglish.practice.data.ExamStatus
import com.example.auraenglish.practice.data.PracticeQuestion
import com.example.auraenglish.practice.data.PracticeSampleData

// Đếm chip bookmark
data class BookmarkChipCounts(
    val all: Int,
    val todo: Int,
    val done: Int
)

// VM câu đã flag
class PracticeBookmarkedViewModel : ViewModel() {

    private val _filter = MutableLiveData(ExamStatus.ALL)
    val filter: LiveData<ExamStatus> = _filter

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery

    private val _questions = MutableLiveData<List<PracticeQuestion>>(emptyList())
    val questions: LiveData<List<PracticeQuestion>> = _questions

    private val _chipCounts = MutableLiveData(BookmarkChipCounts(0, 0, 0))
    val chipCounts: LiveData<BookmarkChipCounts> = _chipCounts

    fun setFilter(status: ExamStatus) {
        _filter.value = status
        refresh()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query.trim()
        refresh()
    }

    fun clearSearch() {
        _searchQuery.value = ""
        refresh()
    }

    fun refresh() {
        _chipCounts.value = BookmarkChipCounts(
            all = PracticeSampleData.flaggedQuestions(ExamStatus.ALL).size,
            todo = PracticeSampleData.flaggedQuestions(ExamStatus.TODO).size,
            done = PracticeSampleData.flaggedQuestions(ExamStatus.DONE).size
        )
        val status = _filter.value ?: ExamStatus.ALL
        val query = _searchQuery.value.orEmpty()
        val data = PracticeSampleData.flaggedQuestions(status).filter { q ->
            if (query.isBlank()) true
            else {
                val s = query.lowercase()
                q.stem.lowercase().contains(s) ||
                    q.tag.lowercase().contains(s) ||
                    q.number.toString() == query
            }
        }
        _questions.value = data
    }
}
