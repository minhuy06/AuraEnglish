package com.example.auraenglish.admin.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdminStudentDataTest {

    private val students = AdminStudentSampleData.create()

    @Test
    fun statusFilters_partitionEverySampleStudent() {
        val active = filterAdminStudents(students, AdminStudentFilter.ACTIVE, "")
        val atRisk = filterAdminStudents(students, AdminStudentFilter.AT_RISK, "")
        val suspended = filterAdminStudents(students, AdminStudentFilter.SUSPENDED, "")

        assertEquals(5, active.size)
        assertEquals(2, atRisk.size)
        assertEquals(1, suspended.size)
        assertEquals(students.size, active.size + atRisk.size + suspended.size)
    }

    @Test
    fun search_ignoresOuterWhitespaceAndLetterCase() {
        val result = filterAdminStudents(
            students,
            AdminStudentFilter.ALL,
            "  NGỮ PHÁP  "
        )

        assertEquals(3, result.size)
        assertTrue(result.all { it.currentCourse == "Ngữ pháp trọng tâm" })
    }

    @Test
    fun search_matchesEmailAndCombinesWithStatusFilter() {
        val suspended = filterAdminStudents(
            students,
            AdminStudentFilter.SUSPENDED,
            "GIAHUY.VO"
        )

        assertEquals(listOf("student_05"), suspended.map { it.id })
    }

    @Test
    fun suspendedStatus_takesPriorityOverLearningRisk() {
        val student = students.first { it.isAtRisk }.copy(isSuspended = true)

        assertEquals(AdminStudentStatus.SUSPENDED, student.status)
    }

    @Test
    fun progressPercent_handlesZeroTotalAndClampsOverflow() {
        val base = students.first()
        val noLessons = base.copy(completedLessons = 0, totalLessons = 0)
        val overflow = base.copy(completedLessons = 12, totalLessons = 10)

        assertEquals(0, noLessons.progressPercent)
        assertEquals(100, overflow.progressPercent)
    }
}
