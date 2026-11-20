package com.example.auraenglish.admin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.auraenglish.admin.data.AdminStudentFilter
import com.example.auraenglish.admin.data.AdminStudentSampleData
import com.example.auraenglish.admin.data.AdminStudentStatus
import com.example.auraenglish.admin.data.AdminStudentUiModel
import com.example.auraenglish.admin.data.filterAdminStudents

data class AdminStudentListState(
    val filter: AdminStudentFilter = AdminStudentFilter.ALL,
    val students: List<AdminStudentUiModel> = emptyList(),
    val allCount: Int = 0,
    val activeCount: Int = 0,
    val atRiskCount: Int = 0,
    val suspendedCount: Int = 0
)

class AdminStudentViewModel : ViewModel() {

    private val _state = MutableLiveData<AdminStudentListState>()
    val state: LiveData<AdminStudentListState> = _state

    private var allStudents = AdminStudentSampleData.create()
    private var activeFilter = AdminStudentFilter.ALL
    private var query = ""

    init {
        emitState()
    }

    fun setFilter(filter: AdminStudentFilter) {
        if (activeFilter == filter) return
        activeFilter = filter
        emitState()
    }

    fun setSearchQuery(text: String) {
        if (query == text) return
        query = text
        emitState()
    }

    fun setSuspended(studentId: String, suspended: Boolean) {
        allStudents = allStudents.map { student ->
            if (student.id == studentId) {
                student.copy(isSuspended = suspended)
            } else {
                student
            }
        }
        emitState()
    }

    private fun emitState() {
        _state.value = AdminStudentListState(
            filter = activeFilter,
            students = filterAdminStudents(allStudents, activeFilter, query),
            allCount = allStudents.size,
            activeCount = allStudents.count { it.status == AdminStudentStatus.ACTIVE },
            atRiskCount = allStudents.count { it.status == AdminStudentStatus.AT_RISK },
            suspendedCount = allStudents.count { it.status == AdminStudentStatus.SUSPENDED }
        )
    }
}
