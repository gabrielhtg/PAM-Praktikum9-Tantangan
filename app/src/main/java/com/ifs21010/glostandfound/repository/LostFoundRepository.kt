package com.ifs21010.glostandfound.repository

import com.ifs21010.glostandfound.entity.LostFound
import com.ifs21010.glostandfound.services.LostFoundDAO

class LostFoundRepository (private val lostfoundDAO : LostFoundDAO) {

    val lostfounds = lostfoundDAO.getAllLostfound()
    val idLostFound = lostfoundDAO.getLostFoundId()

    suspend fun insert (lostfound : LostFound) {
        lostfoundDAO.insertMarkedLostfound(lostfound)
    }

    suspend fun removeMark (id : Int) {
        lostfoundDAO.removeMark(id)
    }
}