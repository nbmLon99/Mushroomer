package com.nbmlon.mushroomer.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nbmlon.mushroomer.model.Mushroom

class MapViewModel : ViewModel() {
    private val markers: MutableLiveData<ArrayList<Mushroom>> by lazy {
        MutableLiveData<ArrayList<Mushroom>>().also {
            loadMarkers()
        }
    }

    fun getMarkers(): LiveData<ArrayList<Mushroom>> {
        return markers
    }

    private fun loadMarkers() {
        // Do an asynchronous operation to fetch users.
    }
}