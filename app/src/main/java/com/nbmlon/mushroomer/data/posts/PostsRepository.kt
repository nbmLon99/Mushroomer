package com.nbmlon.mushroomer.data.posts


import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.nbmlon.mushroomer.model.Post
import com.nbmlon.mushroomer.ui.commu.BoardType
import com.nbmlon.mushroomer.ui.commu.PostSortingOption
import kotlinx.coroutines.flow.Flow

/** 게시판 포스팅 레포지토리 **/

interface PostsRepository {
    /** 게시판 페이징 데이터 가져옴 **/
    fun getPostStream(boardType: BoardType, query: String?, sortOpt : PostSortingOption ): Flow<PagingData<Post>>
}

/** 홈화면 최신 게시글 레포지토리 **/
interface CommuHomeRepository {
    /** 게시판 홈 화면에 띄울 최신글 가져옴 **/
    fun getRecentPostsIntoHome(boardType: BoardType) : ArrayList<Post>
}

fun PostsRepository() : PostsRepository = PostsRepositoryImpl()
fun CommuHomeRepository() : CommuHomeRepository = CommuHomeRepositoryImpl()



private class PostsRepositoryImpl(): PostsRepository {
    companion object {
        const val NETWORK_PAGE_SIZE = 50
    }
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

    override fun getPostStream(boardType: BoardType, query: String?, sortOpt : PostSortingOption): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PostPagingSource(PostsService.create(), query, boardType) }
        ).flow
    }
}

private class CommuHomeRepositoryImpl : CommuHomeRepository {
    override fun getRecentPostsIntoHome(boardType: BoardType): ArrayList<Post> {
        TODO("Not yet implemented")
    }

}