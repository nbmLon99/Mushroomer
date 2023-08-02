package com.nbmlon.mushroomer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nbmlon.mushroomer.model.User

class UserViewModel : ViewModel(){
    private val myProfile: MutableLiveData<User> by lazy {
        MutableLiveData<User>().also {
            loadMyProfile()
        }
    }

    fun getMyProfile(): LiveData<User> {
        return myProfile
    }

    private fun loadMyProfile() {
        // Do an asynchronous operation to fetch users.
    }
}