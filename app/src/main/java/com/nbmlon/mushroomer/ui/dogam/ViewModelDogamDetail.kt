package com.nbmlon.mushroomer.ui.dogam

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbmlon.mushroomer.data.dogam.DogamRepository
import com.nbmlon.mushroomer.domain.DogamUseCaseReqeust
import com.nbmlon.mushroomer.domain.DogamUseCaseResponse
import kotlinx.coroutines.launch

class DogamDetailViewModel : ViewModel() {
    private val repository = DogamRepository()
    private val _response = MutableLiveData<DogamUseCaseResponse.SpecificDogamResponse>()
    val response : LiveData<DogamUseCaseResponse.SpecificDogamResponse> get() = _response
    fun getMush(domain : DogamUseCaseReqeust.SpecificDogamRequestDomain){
        viewModelScope.launch {
            _response.value = repository.getSpecificDogam(domain)
        }
    }
}