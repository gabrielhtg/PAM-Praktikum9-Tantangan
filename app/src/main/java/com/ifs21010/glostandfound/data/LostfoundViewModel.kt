package com.ifs21010.glostandfound.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.ifs21010.glostandfound.entity.LostFound
import com.ifs21010.glostandfound.repository.LostFoundRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LostfoundViewModel (application: Application) : AndroidViewModel(application) {
    val getAllData : LiveData<List<LostFound>>
    val allLostFoundId : LiveData<List<Int>>
    private val repository : LostFoundRepository

    init {
        val lostfoundDao = LostfoundDatabase.getDatabase(application).getLostfoundDao()
        repository = LostFoundRepository(lostfoundDao)
        getAllData = repository.lostfounds
        allLostFoundId = repository.idLostFound
    }

    fun addMarked (lostfound: LostFound) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(lostfound)
        }
    }

    fun removeMark (id: Int) {
        viewModelScope.launch (Dispatchers.IO) {
            repository.removeMark(id)
        }
    }


}