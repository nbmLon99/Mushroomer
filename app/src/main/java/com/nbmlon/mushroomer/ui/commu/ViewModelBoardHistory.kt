package com.nbmlon.mushroomer.ui.commu

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nbmlon.mushroomer.data.posts.CommuHistoryRepository
import com.nbmlon.mushroomer.data.posts.PostsSearchRepository
import com.nbmlon.mushroomer.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


/** 내 댓글, 내 포스트 보여주는화면
 * @param forMyPost : 내 포스팅 눌렀을 시 True, 내 댓글 눌렀을 시 False
 * **/
class BoardHistoryViewModel(
    private val forMyPost : Boolean
) : ViewModel() {
    private val repository : CommuHistoryRepository = CommuHistoryRepository()
    val pagingDataFlow: Flow<PagingData<Post>>
    init{
        pagingDataFlow = loadMyPosts()
    }

    /** 페이징 데이터 요청 **/
    private fun loadMyPosts (): Flow<PagingData<Post>> {
        return if(forMyPost){
            repository.getPostHistoryStream()
        }else repository.getCommentHistoryStream()
    }
}