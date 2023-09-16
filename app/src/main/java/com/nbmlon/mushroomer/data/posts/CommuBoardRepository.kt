package com.nbmlon.mushroomer.data.posts

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.nbmlon.mushroomer.api.service.BoardService
import com.nbmlon.mushroomer.model.Post
import com.nbmlon.mushroomer.ui.commu.board.BoardType
import com.nbmlon.mushroomer.ui.commu.board.PostSortingOption
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


/** 게시판 별 저장소 (페이징 데이터 사용) **/
interface BoardPostsRepository {
    //** 게시판 페이징 데이터 가져옴 **/
    fun getPostStream(boardType: BoardType, sortOpt : PostSortingOption, isHotBoard : Boolean = false): Flow<PagingData<Post>>
}


fun BoardPostsRepository() : BoardPostsRepository = BoardPostsRepositoryImpl()

private class BoardPostsRepositoryImpl: BoardPostsRepository {
    @Inject
    lateinit var backend : BoardService

    override fun getPostStream(boardType: BoardType, sortOpt : PostSortingOption, isHotBoard : Boolean): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PostPagingSource(backend, boardType, isHotBoard = isHotBoard) }
        ).flow
    }
}


private const val NETWORK_PAGE_SIZE = 50
