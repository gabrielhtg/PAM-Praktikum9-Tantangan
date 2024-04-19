package com.ifs21010.glostandfound.services

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ifs21010.glostandfound.entity.LostFound

@Dao
interface LostFoundDAO {
    @Insert
    suspend fun insertMarkedLostfound (lostFound: LostFound)

    @Query("DELETE FROM marked_lostfound_table WHERE lostfound_id = :id")
    suspend fun removeMark(id : Int)

    @Query("SELECT * FROM marked_lostfound_table")
    fun getAllLostfound () : LiveData<List<LostFound>>

    @Query("SELECT lostfound_id FROM marked_lostfound_table")
    fun getLostFoundId() : LiveData<List<Int>>
}