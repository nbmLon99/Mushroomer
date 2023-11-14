package com.nbmlon.mushroomer.data.posts

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.nbmlon.mushroomer.AppUser
import com.nbmlon.mushroomer.api.ResponseCodeConstants.NETWORK_ERROR_CODE
import com.nbmlon.mushroomer.api.ResponseCodeConstants.SUCCESS_CODE
import com.nbmlon.mushroomer.api.ResponseCodeConstants.UNDEFINED_ERROR_CODE
import com.nbmlon.mushroomer.api.service.BoardService
import com.nbmlon.mushroomer.domain.CommuPostUseCaseResponse
import com.nbmlon.mushroomer.model.Post
import com.nbmlon.mushroomer.ui.commu.board.BoardType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.await
import java.io.IOException
import javax.inject.Inject


/** 내 댓글 / 내 포스트 저장소 (페이징 사용)**/
interface CommuHistoryRepository {
    //** 검색된 페이징 데이터 가져옴 **/
    suspend fun getPostHistories() : CommuPostUseCaseResponse.PostsResponseDomain
    suspend fun getCommentHistories() : CommuPostUseCaseResponse.PostsResponseDomain
}

fun CommuHistoryRepository() : CommuHistoryRepository = CommuHistoryRepositoryImpl()




class CommuHistoryRepositoryImpl : CommuHistoryRepository {
    @Inject lateinit var boardBackend : BoardService

    override suspend fun getPostHistories(): CommuPostUseCaseResponse.PostsResponseDomain {
        return try{
            withContext(Dispatchers.IO){
                val result = boardBackend.getUserPosts(AppUser.token!!).await()
                CommuPostUseCaseResponse.PostsResponseDomain(
                    true,
                    SUCCESS_CODE,
                    result.posts.map{ it.toPost() }
                )
            }

        }catch (e : IOException){
            CommuPostUseCaseResponse.PostsResponseDomain(false, NETWORK_ERROR_CODE)
        }catch (e : Exception){
            CommuPostUseCaseResponse.PostsResponseDomain(false, UNDEFINED_ERROR_CODE)
        }
    }

    override suspend fun getCommentHistories():  CommuPostUseCaseResponse.PostsResponseDomain {
        return try{
            withContext(Dispatchers.IO){
                val result = TODO("명세서")
                CommuPostUseCaseResponse.PostsResponseDomain(true,)
            }

        }catch (e : IOException){
            CommuPostUseCaseResponse.PostsResponseDomain(false, NETWORK_ERROR_CODE)
        }catch (e : Exception){
            CommuPostUseCaseResponse.PostsResponseDomain(false, UNDEFINED_ERROR_CODE)
        }
    }

}


