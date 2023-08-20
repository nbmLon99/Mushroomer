package com.nbmlon.mushroomer.data.posts


import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.nbmlon.mushroomer.data.posts.PostsService
import com.nbmlon.mushroomer.model.Post
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface PostsRepository {
    fun getPostStream(query: String): Flow<PagingData<Post>>
}

fun PostsRepository() : PostsRepository = PostsRepositoryImpl()
private class PostsRepositoryImpl: PostsRepository {
    companion object {
        const val NETWORK_PAGE_SIZE = 50
    }

    private val baseUrl : String = ""
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val postsService: PostsService = retrofit.create(PostsService::class.java)

    fun fetchData(): Call<Post> {
        return postsService.getPostsByIdx()
    }



    override fun getPostStream(query: String): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = PostsRepositoryImpl.NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PostPagingSource(PostsService.create(), query) }
        ).flow
    }

}