package com.nbmlon.mushroomer.data.posts

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.nbmlon.mushroomer.api.service.BoardService
import com.nbmlon.mushroomer.model.Post
import com.nbmlon.mushroomer.ui.commu.board.BoardType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject




/** 게시판별 검색 화면 저장소 (페이징 사용 ) **/
interface PostsSearchRepository {
    //** 검색된 페이징 데이터 가져옴 **/
    fun getSearchedPostStream(boardType: BoardType, searchKeyword : String) : Flow<PagingData<Post>>
}

fun PostsSearchRepository() : PostsSearchRepository = PostsSearchRepositoryImpl()


class PostsSearchRepositoryImpl : PostsSearchRepository {
    /** query를 통해 검색된 값 가져오기 **/
    @Inject
    lateinit var backend : BoardService


    override fun getSearchedPostStream(
        boardType: BoardType,
        searchKeyword: String
    ): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PostPagingSource(backend, boardType, searchKeyword) }
        ).flow
    }

}

private const val NETWORK_PAGE_SIZE = 50
