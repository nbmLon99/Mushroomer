package com.nbmlon.mushroomer.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbmlon.mushroomer.data.marker.HistoryRepository
import com.nbmlon.mushroomer.domain.MapUseCaseResponse
import com.nbmlon.mushroomer.model.MushHistory
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {
    private val repository = HistoryRepository()
    private val _markers: MutableLiveData<MapUseCaseResponse.MapResponseDomain> by lazy {
        MutableLiveData<MapUseCaseResponse.MapResponseDomain>().also {
            viewModelScope.launch {
                //it.value = repository.getHistories()
            }
        }
    }
    val markers : LiveData<MapUseCaseResponse.MapResponseDomain> get() = _markers

    fun test(lat : Double, lon : Double){
        _markers.value = MapUseCaseResponse.MapResponseDomain(
            success = true,
            code = 0,
            history = arrayListOf(MushHistory.getDummy(lat, lon))
        )
    }
}