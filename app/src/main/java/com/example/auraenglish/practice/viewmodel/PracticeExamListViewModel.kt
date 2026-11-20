package com.example.auraenglish.practice.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.auraenglish.practice.ExamStatus
import com.example.auraenglish.practice.PracticeExam
import com.example.auraenglish.practice.PracticeSampleData

class PracticeExamListViewModel : ViewModel() {

    private val _filter = MutableLiveData(ExamStatus.ALL)
    val filter: LiveData<ExamStatus> = _filter

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery

    private val _exams = MutableLiveData<List<PracticeExam>>(emptyList())
    val exams: LiveData<List<PracticeExam>> = _exams

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
        val status = _filter.value ?: ExamStatus.ALL
        val query = _searchQuery.value.orEmpty()
        val data = PracticeSampleData.filteredExams(status).filter { exam ->
            if (query.isBlank()) true
            else {
                val q = query.lowercase()
                exam.title.lowercase().contains(q) ||
                    exam.index.contains(q) ||
                    exam.meta.lowercase().contains(q)
            }
        }
        _exams.value = data
    }
}
