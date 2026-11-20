package com.example.auraenglish.admin.data

import java.util.UUID

enum class AdminCourseSkill(val label: String) {
    ALL("Tất cả"),
    VOCAB("Từ vựng"),
    GRAMMAR("Ngữ pháp"),
    LISTEN("Luyện nghe")
}

data class AdminCourseUiModel(
    val id: String,
    val title: String,
    val skill: AdminCourseSkill,
    val cefrLevel: String,
    val metaStats: String,
    val isPublished: Boolean
) {
    val skillTag: String get() = "${skill.label} • $cefrLevel"
}

object AdminCourseStore {

    private val courses = mutableListOf(
        AdminCourseUiModel(
            id = "c1",
            title = "Tiếng Anh công sở",
            skill = AdminCourseSkill.VOCAB,
            cefrLevel = "B1",
            metaStats = "6 chủ đề • 24 bài học • 420 học viên đang học",
            isPublished = true
        ),
        AdminCourseUiModel(
            id = "c2",
            title = "Ngữ pháp trọng tâm",
            skill = AdminCourseSkill.GRAMMAR,
            cefrLevel = "A2",
            metaStats = "10 chuyên đề • 40 bài luyện tập • 680 học viên đang học",
            isPublished = true
        ),
        AdminCourseUiModel(
            id = "c3",
            title = "Luyện nghe & Phản xạ",
            skill = AdminCourseSkill.LISTEN,
            cefrLevel = "B1",
            metaStats = "8 chủ đề • 32 bài nghe • 310 học viên đang học",
            isPublished = true
        ),
        AdminCourseUiModel(
            id = "c4",
            title = "Từ vựng giao tiếp thông dụng",
            skill = AdminCourseSkill.VOCAB,
            cefrLevel = "A2",
            metaStats = "Chưa xuất bản",
            isPublished = false
        )
    )

    fun getAll(): List<AdminCourseUiModel> = courses.toList()

    fun addCourse(
        title: String,
        skill: AdminCourseSkill,
        cefrLevel: String,
        metaStats: String
    ): AdminCourseUiModel {
        val item = AdminCourseUiModel(
            id = "c_${UUID.randomUUID().toString().take(6)}",
            title = title.trim(),
            skill = skill,
            cefrLevel = cefrLevel,
            metaStats = metaStats.trim().ifBlank { "Chưa xuất bản" },
            isPublished = false
        )
        courses.add(0, item)
        return item
    }

    fun updateCourse(
        id: String,
        title: String,
        skill: AdminCourseSkill,
        cefrLevel: String,
        metaStats: String
    ) {
        val idx = courses.indexOfFirst { it.id == id }
        if (idx >= 0) {
            courses[idx] = courses[idx].copy(
                title = title.trim(),
                skill = skill,
                cefrLevel = cefrLevel,
                metaStats = metaStats.trim().ifBlank { courses[idx].metaStats }
            )
        }
    }

    fun setPublished(id: String, published: Boolean) {
        val idx = courses.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val old = courses[idx]
            val newMeta = if (published && old.metaStats == "Chưa xuất bản") {
                "6 chủ đề • 24 bài học • 0 học viên đang học"
            } else {
                old.metaStats
            }
            courses[idx] = old.copy(isPublished = published, metaStats = newMeta)
        }
    }

    fun deleteCourse(id: String) {
        courses.removeAll { it.id == id }
    }
}
