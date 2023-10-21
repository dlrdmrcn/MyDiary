package com.dilara.mydiary.model

data class Diary(
    val date: String,
    val content: String,
    val title: String,
    val mood: Long,
    val downloadUrl: String?,
    val id: String
)