package com.dilara.mydiary.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.dilara.mydiary.model.Diary

@Dao
interface DiaryDao {
    @Query("SELECT * FROM Diary")
    suspend fun getAll(): List<Diary>

    @Insert
    suspend fun insert(diary: Diary) : Long

    @Delete
    suspend fun delete(diary: Diary) : Int

    @Update
    suspend fun update(diary: Diary)

}