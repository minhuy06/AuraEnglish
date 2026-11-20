package com.example.auraenglish.admin.data

enum class AdminStudentFilter {
    ALL,
    ACTIVE,
    AT_RISK,
    SUSPENDED
}

enum class AdminStudentStatus {
    ACTIVE,
    AT_RISK,
    SUSPENDED
}

data class AdminStudentUiModel(
    val id: String,
    val fullName: String,
    val email: String,
    val initials: String,
    val currentCourse: String,
    val completedLessons: Int,
    val totalLessons: Int,
    val streakDays: Int,
    val lastActive: String,
    val joinedDate: String,
    val isAtRisk: Boolean = false,
    val isSuspended: Boolean = false
) {
    val progressPercent: Int
        get() = if (totalLessons == 0) {
            0
        } else {
            ((completedLessons * 100.0) / totalLessons).toInt().coerceIn(0, 100)
        }

    val status: AdminStudentStatus
        get() = when {
            isSuspended -> AdminStudentStatus.SUSPENDED
            isAtRisk -> AdminStudentStatus.AT_RISK
            else -> AdminStudentStatus.ACTIVE
        }
}

object AdminStudentSampleData {

    fun create(): List<AdminStudentUiModel> = listOf(
        AdminStudentUiModel(
            id = "student_01",
            fullName = "Nguyễn An Khang",
            email = "ankhang@gmail.com",
            initials = "AK",
            currentCourse = "Ngữ pháp trọng tâm",
            completedLessons = 32,
            totalLessons = 40,
            streakDays = 12,
            lastActive = "5 phút trước",
            joinedDate = "12/08/2026"
        ),
        AdminStudentUiModel(
            id = "student_02",
            fullName = "Trần Minh Anh",
            email = "minhanh.tran@gmail.com",
            initials = "MA",
            currentCourse = "Tiếng Anh công sở",
            completedLessons = 19,
            totalLessons = 24,
            streakDays = 8,
            lastActive = "18 phút trước",
            joinedDate = "03/09/2026"
        ),
        AdminStudentUiModel(
            id = "student_03",
            fullName = "Lê Hoàng Phúc",
            email = "hoangphuc.le@gmail.com",
            initials = "HP",
            currentCourse = "Luyện nghe & Phản xạ",
            completedLessons = 5,
            totalLessons = 32,
            streakDays = 0,
            lastActive = "12 ngày trước",
            joinedDate = "21/07/2026",
            isAtRisk = true
        ),
        AdminStudentUiModel(
            id = "student_04",
            fullName = "Phạm Thu Hà",
            email = "thuha.pham@gmail.com",
            initials = "TH",
            currentCourse = "Ngữ pháp trọng tâm",
            completedLessons = 27,
            totalLessons = 40,
            streakDays = 6,
            lastActive = "1 giờ trước",
            joinedDate = "16/09/2026"
        ),
        AdminStudentUiModel(
            id = "student_05",
            fullName = "Võ Gia Huy",
            email = "giahuy.vo@gmail.com",
            initials = "GH",
            currentCourse = "Tiếng Anh công sở",
            completedLessons = 11,
            totalLessons = 24,
            streakDays = 0,
            lastActive = "25 ngày trước",
            joinedDate = "28/06/2026",
            isSuspended = true
        ),
        AdminStudentUiModel(
            id = "student_06",
            fullName = "Nguyễn Ngọc Mai",
            email = "ngocmai@gmail.com",
            initials = "NM",
            currentCourse = "Từ vựng giao tiếp thông dụng",
            completedLessons = 18,
            totalLessons = 24,
            streakDays = 15,
            lastActive = "2 giờ trước",
            joinedDate = "05/10/2026"
        ),
        AdminStudentUiModel(
            id = "student_07",
            fullName = "Bùi Đức Long",
            email = "duclong.bui@gmail.com",
            initials = "ĐL",
            currentCourse = "Luyện nghe & Phản xạ",
            completedLessons = 7,
            totalLessons = 32,
            streakDays = 1,
            lastActive = "8 ngày trước",
            joinedDate = "19/08/2026",
            isAtRisk = true
        ),
        AdminStudentUiModel(
            id = "student_08",
            fullName = "Đặng Khánh Linh",
            email = "khanhlinh.dang@gmail.com",
            initials = "KL",
            currentCourse = "Ngữ pháp trọng tâm",
            completedLessons = 36,
            totalLessons = 40,
            streakDays = 21,
            lastActive = "Vừa xong",
            joinedDate = "30/05/2026"
        )
    )
}

fun filterAdminStudents(
    students: List<AdminStudentUiModel>,
    filter: AdminStudentFilter,
    query: String
): List<AdminStudentUiModel> {
    val normalizedQuery = query.trim()
    return students.filter { student ->
        val matchesFilter = when (filter) {
            AdminStudentFilter.ALL -> true
            AdminStudentFilter.ACTIVE -> student.status == AdminStudentStatus.ACTIVE
            AdminStudentFilter.AT_RISK -> student.status == AdminStudentStatus.AT_RISK
            AdminStudentFilter.SUSPENDED -> student.status == AdminStudentStatus.SUSPENDED
        }
        val matchesQuery = normalizedQuery.isBlank() ||
            student.fullName.contains(normalizedQuery, ignoreCase = true) ||
            student.email.contains(normalizedQuery, ignoreCase = true) ||
            student.currentCourse.contains(normalizedQuery, ignoreCase = true)
        matchesFilter && matchesQuery
    }
}
