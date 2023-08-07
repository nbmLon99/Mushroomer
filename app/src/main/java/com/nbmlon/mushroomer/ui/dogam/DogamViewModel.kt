package com.nbmlon.mushroomer.ui.dogam

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.nbmlon.mushroomer.data.dogam.DogamPagingSource
import com.nbmlon.mushroomer.model.Dogam
import com.nbmlon.mushroomer.model.Mushroom

class DogamViewModel : ViewModel() {
    private val _dogam : MutableLiveData<Dogam> by lazy {
        MutableLiveData<Dogam>().also {
            loadDogam()
        }
    }
    val flow = Pager(
        // Configure how data is loaded by passing additional properties to
        // PagingConfig, such as prefetchDistance.
        PagingConfig(pageSize = 20)
    ) {
        DogamPagingSource(backend, query)
    }.flow
        .cachedIn(viewModelScope)

    val pages : LiveData<ArrayList<Mushroom>> get() = _pages

    private fun loadPaging(paging : Int) {
        // Do an asynchronous operation to fetch users.
    }


    //내에서 페이지 생성 해야할듯?
    private fun loadDogam(){
        // Do an asynchronous operation to fetch users.
    }



    private fun loadMyDogam(){

    }

    fun findPaging(dogamNo : Int) :Int{
        return 0
    }
}