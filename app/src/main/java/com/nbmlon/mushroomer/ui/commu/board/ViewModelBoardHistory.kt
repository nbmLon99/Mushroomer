package com.nbmlon.mushroomer.ui.commu.board

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbmlon.mushroomer.data.posts.CommuHistoryRepository
import com.nbmlon.mushroomer.domain.CommuPostUseCaseResponse
import com.nbmlon.mushroomer.model.Post
import kotlinx.coroutines.launch


/** 내 댓글, 내 포스트 보여주는화면
 * @param forMyPost : 내 포스팅 눌렀을 시 True, 내 댓글 눌렀을 시 False
 * **/
class ViewModelBoardHistory(
    private val forMyPost : Boolean
) : ViewModel() {
    private val repository : CommuHistoryRepository = CommuHistoryRepository()
    val loadedPosts = MutableLiveData<CommuPostUseCaseResponse.PostsResponseDomain>()

    /** 페이징 데이터 요청 **/
    fun loadMyPosts () {
        viewModelScope.launch {
            loadedPosts.value = if(forMyPost){
                repository.getPostHistories()
            }else repository.getCommentHistories()
        }
    }
}