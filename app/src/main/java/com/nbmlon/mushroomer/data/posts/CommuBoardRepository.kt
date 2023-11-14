package com.nbmlon.mushroomer.data.posts

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.nbmlon.mushroomer.api.ResponseCodeConstants
import com.nbmlon.mushroomer.api.service.BoardService
import com.nbmlon.mushroomer.domain.CommuPostUseCaseResponse
import com.nbmlon.mushroomer.ui.commu.board.BoardType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await
import java.io.IOException
import javax.inject.Inject


/** 게시판 별 저장소 (페이징 데이터 사용) **/
interface BoardPostsRepository {
    //** 게시판 페이징 데이터 가져옴 **/
    suspend fun loadPosts(boardType: BoardType): CommuPostUseCaseResponse.PostsResponseDomain
}


fun BoardPostsRepository() : BoardPostsRepository = BoardPostsRepositoryImpl()

private class BoardPostsRepositoryImpl: BoardPostsRepository {
    @Inject
    lateinit var backend : BoardService

    override suspend fun loadPosts(boardType: BoardType): CommuPostUseCaseResponse.PostsResponseDomain {
        return try{
            withContext(Dispatchers.IO){
                val result = backend.getBoardPosts(boardType.serverName).await()
                CommuPostUseCaseResponse.PostsResponseDomain(true,
                    ResponseCodeConstants.SUCCESS_CODE, result.posts.map{it.toPost()})
            }
        }catch (e : IOException){
            CommuPostUseCaseResponse.PostsResponseDomain(false, ResponseCodeConstants.NETWORK_ERROR_CODE)
        }catch (e : Exception){
            CommuPostUseCaseResponse.PostsResponseDomain(false, ResponseCodeConstants.UNDEFINED_ERROR_CODE)
        }
    }
}


private const val NETWORK_PAGE_SIZE = 50
