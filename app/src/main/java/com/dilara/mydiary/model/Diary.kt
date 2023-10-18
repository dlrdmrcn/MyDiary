package com.dilara.mydiary.model

data class Diary(
    val diaryDate: String,
    val diaryEditText: String,
    val diaryTitle: String,
    val diaryMood: Long,
    val downloadUrl: String?
)