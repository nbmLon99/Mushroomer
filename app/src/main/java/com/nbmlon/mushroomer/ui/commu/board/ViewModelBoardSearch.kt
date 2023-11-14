package com.nbmlon.mushroomer.ui.commu.board

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbmlon.mushroomer.data.posts.PostsSearchRepository
import com.nbmlon.mushroomer.domain.CommuPostUseCaseResponse
import kotlinx.coroutines.launch


/**
 * 게시판 검색을 위한 Viewmodel
 * **/
class ViewModelBoardSearch(
    private val repository : PostsSearchRepository,
    private val savedStateHandle: SavedStateHandle,
    private val boardType: BoardType
) : ViewModel() {

    val loadedPosts = MutableLiveData<CommuPostUseCaseResponse.PostsResponseDomain>()

    fun searchPosts(keyword : String){
        viewModelScope.launch {
            loadedPosts.value = repository.searchPosts(keyword)
        }
    }

}
