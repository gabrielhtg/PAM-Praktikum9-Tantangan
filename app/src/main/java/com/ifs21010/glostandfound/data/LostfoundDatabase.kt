package com.ifs21010.glostandfound.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ifs21010.glostandfound.entity.LostFound
import com.ifs21010.glostandfound.services.LostFoundDAO

@Database(entities = [LostFound::class], version = 1, exportSchema = false)
abstract class LostfoundDatabase : RoomDatabase() {

    abstract fun getLostfoundDao() : LostFoundDAO

    companion object {
        @Volatile
        private var instance : LostfoundDatabase? = null

        fun getDatabase(context : Context) : LostfoundDatabase {
            val tempInstance = instance

            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance2 = Room.databaseBuilder(
                    context.applicationContext,
                    LostfoundDatabase::class.java,
                    "lostfound_db"
                ).build()

                instance = instance2
                return instance as LostfoundDatabase
            }
        }
    }

}