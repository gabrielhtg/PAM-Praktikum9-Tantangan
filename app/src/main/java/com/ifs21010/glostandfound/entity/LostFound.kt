package com.ifs21010.glostandfound.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "marked_lostfound_table")
data class LostFound (
    @PrimaryKey(autoGenerate = true)
    val id : Long,
    @ColumnInfo("lostfound_id")
    val lostfoundId : Int
)