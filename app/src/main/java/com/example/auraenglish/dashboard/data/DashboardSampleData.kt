package com.example.auraenglish.dashboard.data

import com.example.auraenglish.R

enum class CourseFilter { ALL, VOCAB, GRAMMAR, EXAM, LISTEN }

enum class StoreFilter { ALL, OWNED, NOT_OWNED }

data class DashCourse(
    val title: String,
    val topics: Int,
    val lessons: Int,
    val done: Int,
    val total: Int,
    val filter: CourseFilter,
    val accentRes: Int,
    val iconRes: Int,
    val doneLabel: Boolean = false
)

data class DashStoreItem(
    val index: String,
    val title: String,
    val meta: String,
    val owned: Boolean,
    val price: String = ""
)

object DashboardSampleData {

    val courses = listOf(
        DashCourse("Tiếng anh công sở", 6, 24, 12, 24, CourseFilter.VOCAB, R.drawable.bg_dash_skill_vocab, R.drawable.ic_dash_vocab),
        DashCourse("Luyện nghe & Phản xạ", 8, 32, 4, 32, CourseFilter.VOCAB, R.drawable.bg_dash_skill_listen, R.drawable.ic_dash_listen),
        DashCourse("Ngữ pháp trọng tâm", 0, 40, 38, 40, CourseFilter.GRAMMAR, R.drawable.bg_dash_skill_grammar, R.drawable.ic_dash_grammar, doneLabel = true),
        DashCourse("Từ vựng thông dụng", 15, 60, 10, 48, CourseFilter.VOCAB, R.drawable.bg_dash_skill_vocab, R.drawable.ic_dash_vocab),
        DashCourse("Từ vựng giao tiếp", 12, 48, 12, 48, CourseFilter.VOCAB, R.drawable.bg_dash_skill_vocab, R.drawable.ic_dash_vocab)
        // Tab Luyện đề dùng PracticeSampleData.exams — không list course giả
    )

    val storeItems = listOf(
        DashStoreItem("01", "Bộ đề Toeic - Nâng cao", "60 đề - Listening + Reading", owned = true),
        DashStoreItem("02", "Luyện Part 5 nâng cao", "20 đề · 600 câu", owned = false, price = "99.000")
    )

    fun filteredCourses(filter: CourseFilter, query: String): List<DashCourse> {
        val base = if (filter == CourseFilter.ALL) courses else courses.filter { it.filter == filter }
        if (query.isBlank()) return base
        val q = query.lowercase()
        return base.filter { it.title.lowercase().contains(q) }
    }

    fun filteredStore(filter: StoreFilter, query: String): List<DashStoreItem> {
        val base = when (filter) {
            StoreFilter.ALL -> storeItems
            StoreFilter.OWNED -> storeItems.filter { it.owned }
            StoreFilter.NOT_OWNED -> storeItems.filter { !it.owned }
        }
        if (query.isBlank()) return base
        val q = query.lowercase()
        return base.filter { it.title.lowercase().contains(q) || it.meta.lowercase().contains(q) }
    }
}
