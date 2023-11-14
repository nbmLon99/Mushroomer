package com.nbmlon.mushroomer.data.posts

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.nbmlon.mushroomer.api.ResponseCodeConstants
import com.nbmlon.mushroomer.api.ResponseCodeConstants.SUCCESS_CODE
import com.nbmlon.mushroomer.api.service.BoardService
import com.nbmlon.mushroomer.domain.CommuPostUseCaseResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await
import java.io.IOException
import javax.inject.Inject




/** 게시판별 검색 화면 저장소 (페이징 사용 ) **/
interface PostsSearchRepository {
    suspend fun searchPosts(searchKeyword : String) : CommuPostUseCaseResponse.PostsResponseDomain
}

fun PostsSearchRepository() : PostsSearchRepository = PostsSearchRepositoryImpl()


class PostsSearchRepositoryImpl : PostsSearchRepository {
    @Inject
    lateinit var backend : BoardService


    override suspend fun searchPosts(searchKeyword : String) : CommuPostUseCaseResponse.PostsResponseDomain{
        return try{
            withContext(Dispatchers.IO){
                val result = backend.searchBoard(searchKeyword).await()
                CommuPostUseCaseResponse.PostsResponseDomain(true,SUCCESS_CODE, result.posts.map{it.toPost()})
            }
        }catch (e : IOException){
            CommuPostUseCaseResponse.PostsResponseDomain(false, ResponseCodeConstants.NETWORK_ERROR_CODE)
        }catch (e : Exception){
            CommuPostUseCaseResponse.PostsResponseDomain(false, ResponseCodeConstants.UNDEFINED_ERROR_CODE)
        }
    }

}
