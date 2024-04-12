package com.ifs21010.glostandfound.services

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ifs21010.glostandfound.entity.LostFound

@Database(entities = [LostFound::class], version = 1)
abstract class LostFoundDatabase : RoomDatabase(){

    abstract val lostFoundDAO : LostFoundDAO

    companion object {
        @Volatile
        private var INSTANCE : LostFoundDatabase ?= null

        fun getInstance (context : Context) : LostFoundDatabase{
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        LostFoundDatabase::class.java,
                        "marked_lostfound_table"
                    ).build()
                }

                INSTANCE = instance
                return instance
            }
        }
    }

}