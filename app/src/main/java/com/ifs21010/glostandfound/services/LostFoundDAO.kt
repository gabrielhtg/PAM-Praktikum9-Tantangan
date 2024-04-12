package com.ifs21010.glostandfound.services

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ifs21010.glostandfound.entity.LostFound

@Dao
interface LostFoundDAO {
    @Insert
    suspend fun insertMarkedLostfound (lostFound : LostFound): Long

    @Delete
    suspend fun deleteMarkedLostFound (lostFound: LostFound)

    @Query("SELECT * FROM marked_lostfound_table")
    fun getAllLostfound () : LiveData<List<LostFound>>
}