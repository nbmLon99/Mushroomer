package com.nbmlon.mushroomer.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbmlon.mushroomer.data.user.ProfileRepository
import com.nbmlon.mushroomer.domain.ProfileUseCaseRequest
import com.nbmlon.mushroomer.domain.ProfileUseCaseResponse
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    val repository = ProfileRepository()
    private val _response = MutableLiveData<ProfileUseCaseResponse>()
    val response : LiveData<ProfileUseCaseResponse> = _response
    val request : (ProfileUseCaseRequest) -> Unit

    init {
        request = {domain ->
            viewModelScope.launch {
                when(domain){
                    is ProfileUseCaseRequest.ModifyProfileRequestDomain ->{
                        _response.value = repository.modifyProfile(domain)
                    }
                    is ProfileUseCaseRequest.WithdrawalRequestDomain ->{
                        _response.value = repository.withdrawal(domain)

                    }
                }
            }
        }
    }


}