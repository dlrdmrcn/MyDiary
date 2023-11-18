package com.dilara.mydiary.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dilara.mydiary.model.Diary

@Database(entities = [Diary::class], version = 2, exportSchema = true)
abstract class DiaryDatabase : RoomDatabase() {
    abstract fun diaryDao(): DiaryDao
}