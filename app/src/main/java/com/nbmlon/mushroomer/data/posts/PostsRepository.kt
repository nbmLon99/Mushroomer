package com.nbmlon.mushroomer.data.posts


import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.nbmlon.mushroomer.AppUser
import com.nbmlon.mushroomer.model.Post
import com.nbmlon.mushroomer.ui.commu.BoardType
import com.nbmlon.mushroomer.ui.commu.PostSortingOption
import kotlinx.coroutines.flow.Flow


/** 홈화면 저장소 **/
interface CommuHomeRepository {
    // 게시판 홈 화면에 띄울 최신글 가져옴
    fun getRecentPostsIntoHome(boardType: BoardType) : ArrayList<Post>
}


/** 게시판 별 저장소 **/
interface PostsRepository {
    //** 게시판 페이징 데이터 가져옴 **/
    fun getPostStream(boardType: BoardType, sortOpt : PostSortingOption , isHotBoard : Boolean = false): Flow<PagingData<Post>>
}
/** 게시판별 검색 화면 저장소 **/
interface PostsSearchRepository {
    //** 검색된 페이징 데이터 가져옴 **/
    fun getSearchedPostStream(boardType: BoardType, searchKeyword : String) : Flow<PagingData<Post>>
}

/** 내 댓글 / 내 포스트 저장소 **/
interface CommuHistoryRepository {
    //** 검색된 페이징 데이터 가져옴 **/
    fun getPostHistoryStream() : Flow<PagingData<Post>>
    fun getCommentHistoryStream() : Flow<PagingData<Post>>
}





fun PostsRepository() : PostsRepository = PostsRepositoryImpl()
fun CommuHomeRepository() : CommuHomeRepository = CommuHomeRepositoryImpl()
fun PostsSearchRepository() : PostsSearchRepository = PostsSearchRepositoryImpl()
fun CommuHistoryRepository() : CommuHistoryRepository = CommuHistoryRepositoryImpl()




class CommuHistoryRepositoryImpl : CommuHistoryRepository {
    override fun getPostHistoryStream(): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PostPagingSource(PostsService.create(), BoardType.MyPosts) }
        ).flow
    }

    override fun getCommentHistoryStream(): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PostPagingSource(PostsService.create(), BoardType.MyComments) }
        ).flow
    }

}


class PostsSearchRepositoryImpl : PostsSearchRepository {
    /** query를 통해 검색된 값 가져오기 **/

    override fun getSearchedPostStream(
        boardType: BoardType,
        searchKeyword: String
    ): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PostPagingSource(PostsService.create(), boardType, searchKeyword) }
        ).flow
    }

}


private class PostsRepositoryImpl: PostsRepository {
//
//    private val baseUrl : String = ""
//    private val retrofit: Retrofit = Retrofit.Builder()
//        .baseUrl(baseUrl)
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()

//    private val postsService: PostsService = retrofit.create(PostsService::class.java)
//
//    fun fetchData(): Call<Post> {
//        return postsService.getPostsByIdx()
//    }
//
//

    override fun getPostStream(boardType: BoardType, sortOpt : PostSortingOption, isHotBoard : Boolean): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PostPagingSource(PostsService.create(), boardType, isHotBoard = isHotBoard) }
        ).flow
    }
}

private class CommuHomeRepositoryImpl : CommuHomeRepository {
    override fun getRecentPostsIntoHome(boardType: BoardType): ArrayList<Post> {
        TODO("Not yet implemented")
    }

}

private const val NETWORK_PAGE_SIZE = 50
