package com.nbmlon.mushroomer.ui.commu.board

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbmlon.mushroomer.data.posts.BoardPostsRepository
import com.nbmlon.mushroomer.domain.CommuPostUseCaseResponse
import kotlinx.coroutines.launch


/**
 *  게시판 페이징 데이터를 위한 viewModel
 *  **/
class ViewModelBoard(
    private val repository : BoardPostsRepository,
    private val savedStateHandle: SavedStateHandle,
    boardType: BoardType
) : ViewModel() {
    val loadedPosts = MutableLiveData<CommuPostUseCaseResponse.PostsResponseDomain>()
    fun fetchPosts(boardType: BoardType){
        viewModelScope.launch {
            loadedPosts.value = repository.loadPosts(boardType)
        }
    }

}