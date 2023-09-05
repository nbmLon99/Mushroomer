package com.nbmlon.mushroomer.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nbmlon.mushroomer.model.MushHistory

class MapViewModel : ViewModel() {
    private val _markers: MutableLiveData<ArrayList<MushHistory>> by lazy {
        MutableLiveData<ArrayList<MushHistory>>().also {
            loadHistories()
        }
    }
    val markers : LiveData<ArrayList<MushHistory>> get() = _markers



    private fun loadHistories() {
        // Do an asynchronous operation to fetch users.
    }
}