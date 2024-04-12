package com.ifs21010.glostandfound.repository

import com.ifs21010.glostandfound.entity.LostFound
import com.ifs21010.glostandfound.services.LostFoundDAO

class LostFoundRepository (private val lostfoundDAO : LostFoundDAO) {

    val lostfounds = lostfoundDAO.getAllLostfound()

    suspend fun insert (lostfound : LostFound) : Long {
        return lostfoundDAO.insertMarkedLostfound(lostfound)
    }

    suspend fun delete (lostfound: LostFound) {
        return lostfoundDAO.deleteMarkedLostFound(lostfound)
    }

}