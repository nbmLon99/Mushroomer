package com.nbmlon.mushroomer.ui.dogam

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nbmlon.mushroomer.model.Mushroom

class DogamViewModel : ViewModel() {
    private val pages : MutableLiveData<ArrayList<Mushroom>> by lazy {
        MutableLiveData<ArrayList<Mushroom>>().also {
            loadPaging(0)
        }
    }

    fun getPages(): LiveData<ArrayList<Mushroom>> {
        return pages
    }

    private fun loadPaging(paging : Int) {
        // Do an asynchronous operation to fetch users.
    }

    fun findPaging(dogamNo : Int) :Int{
        return 0
    }
}