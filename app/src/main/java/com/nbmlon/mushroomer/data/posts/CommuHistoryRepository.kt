package com.nbmlon.mushroomer.data.posts

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.nbmlon.mushroomer.api.service.BoardService
import com.nbmlon.mushroomer.api.service.CommentService
import com.nbmlon.mushroomer.model.Post
import com.nbmlon.mushroomer.ui.commu.board.BoardType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


/** 내 댓글 / 내 포스트 저장소 (페이징 사용)**/
interface CommuHistoryRepository {
    //** 검색된 페이징 데이터 가져옴 **/
    fun getPostHistoryStream() : Flow<PagingData<Post>>
    fun getCommentHistoryStream() : Flow<PagingData<Post>>
}

fun CommuHistoryRepository() : CommuHistoryRepository = CommuHistoryRepositoryImpl()




class CommuHistoryRepositoryImpl : CommuHistoryRepository {
    @Inject
    private lateinit var boardBackend : BoardService
    @Inject
    private lateinit var commentBackend : CommentService

    override fun getPostHistoryStream(): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PostPagingSource(boardBackend, BoardType.MyPosts) }
        ).flow
    }

    override fun getCommentHistoryStream(): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PostPagingSource(boardBackend, BoardType.MyComments) }
        ).flow
    }

}



private const val NETWORK_PAGE_SIZE = 50
