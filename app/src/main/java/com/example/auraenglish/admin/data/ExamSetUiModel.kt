package com.example.auraenglish.admin.data

// Model UI bộ đề admin
data class ExamSetUiModel(
    val index: String,
    val title: String,
    val meta: String,
    val status: String,
    val isPaid: Boolean,
    val priceLabel: String? = null,
    val examCount: Int = 0,
    val description: String = ""
)
