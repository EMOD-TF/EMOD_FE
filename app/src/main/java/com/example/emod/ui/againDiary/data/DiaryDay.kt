package com.example.emod.ui.againDiary.data

data class DiaryDay(
    val date: String,            // 예: "2025-07-09" (yyyy-MM-dd)
    val dayOfWeek: String,       // 예: "수"
    val summary: String?,         // null이면 일기가 없는 상태
    val emotion: String?
)