package com.example.auraenglish.admin.data

enum class AdminStatisticsPeriod {
    WEEK,
    MONTH,
    YEAR
}

data class AdminActivityPoint(
    val label: String,
    val learners: Int
)

data class AdminCoursePerformance(
    val title: String,
    val learnerCount: Int,
    val completionPercent: Int
)

data class AdminStatisticsUiModel(
    val period: AdminStatisticsPeriod,
    val totalStudents: Int,
    val totalStudentsChange: Double,
    val activeStudents: Int,
    val activeStudentsChange: Double,
    val completionPercent: Int,
    val completionChange: Double,
    val revenueVnd: Long,
    val revenueChange: Double,
    val activity: List<AdminActivityPoint>,
    val topCourses: List<AdminCoursePerformance>
)

object AdminStatisticsSampleData {

    fun get(period: AdminStatisticsPeriod): AdminStatisticsUiModel = when (period) {
        AdminStatisticsPeriod.WEEK -> AdminStatisticsUiModel(
            period = period,
            totalStudents = 1_248,
            totalStudentsChange = 8.4,
            activeStudents = 386,
            activeStudentsChange = 12.6,
            completionPercent = 74,
            completionChange = 3.2,
            revenueVnd = 18_450_000L,
            revenueChange = 6.8,
            activity = listOf(
                AdminActivityPoint("T2", 214),
                AdminActivityPoint("T3", 286),
                AdminActivityPoint("T4", 263),
                AdminActivityPoint("T5", 342),
                AdminActivityPoint("T6", 318),
                AdminActivityPoint("T7", 271),
                AdminActivityPoint("CN", 229)
            ),
            topCourses = defaultTopCourses()
        )

        AdminStatisticsPeriod.MONTH -> AdminStatisticsUiModel(
            period = period,
            totalStudents = 1_248,
            totalStudentsChange = 12.1,
            activeStudents = 917,
            activeStudentsChange = 9.3,
            completionPercent = 71,
            completionChange = 4.7,
            revenueVnd = 72_800_000L,
            revenueChange = 11.5,
            activity = listOf(
                AdminActivityPoint("Tuần 1", 624),
                AdminActivityPoint("Tuần 2", 751),
                AdminActivityPoint("Tuần 3", 843),
                AdminActivityPoint("Tuần 4", 917)
            ),
            topCourses = listOf(
                AdminCoursePerformance("Ngữ pháp trọng tâm", 680, 79),
                AdminCoursePerformance("Tiếng Anh công sở", 420, 75),
                AdminCoursePerformance("Luyện nghe & Phản xạ", 310, 69)
            )
        )

        AdminStatisticsPeriod.YEAR -> AdminStatisticsUiModel(
            period = period,
            totalStudents = 1_248,
            totalStudentsChange = 38.6,
            activeStudents = 1_106,
            activeStudentsChange = 24.8,
            completionPercent = 68,
            completionChange = 7.9,
            revenueVnd = 684_500_000L,
            revenueChange = 31.4,
            activity = listOf(
                AdminActivityPoint("T1", 438),
                AdminActivityPoint("T3", 562),
                AdminActivityPoint("T5", 704),
                AdminActivityPoint("T7", 815),
                AdminActivityPoint("T9", 926),
                AdminActivityPoint("T11", 1_038),
                AdminActivityPoint("T12", 1_106)
            ),
            topCourses = listOf(
                AdminCoursePerformance("Ngữ pháp trọng tâm", 680, 76),
                AdminCoursePerformance("Tiếng Anh công sở", 420, 72),
                AdminCoursePerformance("Luyện nghe & Phản xạ", 310, 66)
            )
        )
    }

    private fun defaultTopCourses(): List<AdminCoursePerformance> = listOf(
        AdminCoursePerformance("Ngữ pháp trọng tâm", 680, 82),
        AdminCoursePerformance("Tiếng Anh công sở", 420, 78),
        AdminCoursePerformance("Luyện nghe & Phản xạ", 310, 73)
    )
}
