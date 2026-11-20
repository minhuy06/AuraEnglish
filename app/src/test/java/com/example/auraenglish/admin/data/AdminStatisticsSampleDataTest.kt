package com.example.auraenglish.admin.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdminStatisticsSampleDataTest {

    @Test
    fun get_returnsDataForRequestedPeriod() {
        AdminStatisticsPeriod.entries.forEach { period ->
            val state = AdminStatisticsSampleData.get(period)

            assertEquals(period, state.period)
            assertTrue(state.totalStudents > 0)
            assertTrue(state.activeStudents in 0..state.totalStudents)
            assertTrue(state.activity.isNotEmpty())
            assertEquals(3, state.topCourses.size)
        }
    }

    @Test
    fun get_keepsPercentagesInsideDisplayRange() {
        AdminStatisticsPeriod.entries.forEach { period ->
            val state = AdminStatisticsSampleData.get(period)

            assertTrue(state.completionPercent in 0..100)
            assertTrue(
                state.topCourses.all { course ->
                    course.completionPercent in 0..100
                }
            )
        }
    }

    @Test
    fun longerPeriod_containsHigherRevenueTotal() {
        val week = AdminStatisticsSampleData.get(AdminStatisticsPeriod.WEEK)
        val month = AdminStatisticsSampleData.get(AdminStatisticsPeriod.MONTH)
        val year = AdminStatisticsSampleData.get(AdminStatisticsPeriod.YEAR)

        assertTrue(month.revenueVnd > week.revenueVnd)
        assertTrue(year.revenueVnd > month.revenueVnd)
    }
}
