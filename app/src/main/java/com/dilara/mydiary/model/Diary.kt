package com.dilara.mydiary.model

data class Diary(
    var date: String,
    var content: String,
    var title: String,
    var mood: Long,
    var downloadUrl: String?,
    var id: String
)