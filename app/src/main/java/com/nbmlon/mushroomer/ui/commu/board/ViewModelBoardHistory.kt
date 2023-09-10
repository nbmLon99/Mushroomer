package com.nbmlon.mushroomer.ui.commu.board

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.nbmlon.mushroomer.data.posts.CommuHistoryRepository
import com.nbmlon.mushroomer.model.Post
import kotlinx.coroutines.flow.Flow


/** 내 댓글, 내 포스트 보여주는화면
 * @param forMyPost : 내 포스팅 눌렀을 시 True, 내 댓글 눌렀을 시 False
 * **/
class ViewModelBoardHistory(
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