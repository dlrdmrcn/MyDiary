package com.dilara.mydiary.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Diary(
    @ColumnInfo(name = "date")
    var date: String,
    @ColumnInfo(name = "content")
    var content: String,
    @ColumnInfo(name = "title")
    var title: String,
    @ColumnInfo(name = "mood")
    var mood: Long,
    @ColumnInfo(name = "downloadUrl")
    var downloadUrl: String?,
    var id: String
) {
    @PrimaryKey(autoGenerate = true)
    var roomId: Int = 0
}