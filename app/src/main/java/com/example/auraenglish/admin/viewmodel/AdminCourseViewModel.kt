package com.example.auraenglish.admin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.auraenglish.admin.data.AdminCourseSkill
import com.example.auraenglish.admin.data.AdminCourseStore
import com.example.auraenglish.admin.data.AdminCourseUiModel

data class AdminCourseListState(
    val filter: AdminCourseSkill = AdminCourseSkill.ALL,
    val courses: List<AdminCourseUiModel> = emptyList(),
    val allCount: Int = 0,
    val vocabCount: Int = 0,
    val grammarCount: Int = 0,
    val listenCount: Int = 0
)

class AdminCourseViewModel : ViewModel() {

    private val _state = MutableLiveData<AdminCourseListState>()
    val state: LiveData<AdminCourseListState> = _state

    private var activeFilter: AdminCourseSkill = AdminCourseSkill.ALL
    private var query: String = ""

    init {
        emitState()
    }

    fun setFilter(filter: AdminCourseSkill) {
        activeFilter = filter
        emitState()
    }

    fun setSearchQuery(text: String) {
        query = text
        emitState()
    }

    fun addCourse(title: String, skill: AdminCourseSkill, cefrLevel: String, metaStats: String) {
        AdminCourseStore.addCourse(title, skill, cefrLevel, metaStats)
        emitState()
    }

    fun updateCourse(id: String, title: String, skill: AdminCourseSkill, cefrLevel: String, metaStats: String) {
        AdminCourseStore.updateCourse(id, title, skill, cefrLevel, metaStats)
        emitState()
    }

    fun setPublished(id: String, published: Boolean) {
        AdminCourseStore.setPublished(id, published)
        emitState()
    }

    fun deleteCourse(id: String) {
        AdminCourseStore.deleteCourse(id)
        emitState()
    }

    private fun emitState() {
        val all = AdminCourseStore.getAll()
        val q = query.trim()

        val filtered = all.filter { item ->
            val matchFilter = activeFilter == AdminCourseSkill.ALL || item.skill == activeFilter
            val matchQuery = q.isBlank() ||
                item.title.contains(q, ignoreCase = true) ||
                item.skillTag.contains(q, ignoreCase = true) ||
                item.metaStats.contains(q, ignoreCase = true)
            matchFilter && matchQuery
        }

        _state.value = AdminCourseListState(
            filter = activeFilter,
            courses = filtered,
            allCount = all.size,
            vocabCount = all.count { it.skill == AdminCourseSkill.VOCAB },
            grammarCount = all.count { it.skill == AdminCourseSkill.GRAMMAR },
            listenCount = all.count { it.skill == AdminCourseSkill.LISTEN }
        )
    }
}
