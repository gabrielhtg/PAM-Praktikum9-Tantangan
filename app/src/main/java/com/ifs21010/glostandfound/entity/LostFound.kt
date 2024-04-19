package com.ifs21010.glostandfound.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "marked_lostfound_table", indices = [Index(value = ["lostfound_id"], unique = true)])
data class LostFound (
    @PrimaryKey(autoGenerate = true)
    val id : Int,

    @ColumnInfo("lostfound_id")
    val lostfoundId : Int
)