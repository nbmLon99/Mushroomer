package com.nbmlon.mushroomer.data.posts

import com.nbmlon.mushroomer.api.ResponseCodeConstants
import com.nbmlon.mushroomer.api.ResponseCodeConstants.SUCCESS_CODE
import com.nbmlon.mushroomer.api.service.BoardService
import com.nbmlon.mushroomer.domain.CommuHomeUseCaseResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await
import java.io.IOException
import javax.inject.Inject

/** 홈화면 저장소 **/
interface CommuHomeRepository {
    // 게시판 홈 화면에 띄울 최신글 가져옴
    suspend fun getRecentPostsIntoHome() : CommuHomeUseCaseResponse.CommuHomeDomain
}


fun CommuHomeRepository() : CommuHomeRepository = CommuHomeRepositoryImpl()

private class CommuHomeRepositoryImpl : CommuHomeRepository {
    @Inject lateinit var backend : BoardService
    override suspend fun getRecentPostsIntoHome(): CommuHomeUseCaseResponse.CommuHomeDomain {
        return try{
            withContext(Dispatchers.IO){
                TODO("명세서")
                val result = backend.getRecentBoard().await()
                CommuHomeUseCaseResponse.CommuHomeDomain(
                    success = true ,
                    code = SUCCESS_CODE,
                    freeBoardPosts = null,
                    qnaBoardPosts = null,
                    picBoardPosts = null
                )
            }
        }catch (e : IOException){
            CommuHomeUseCaseResponse.CommuHomeDomain(false, ResponseCodeConstants.NETWORK_ERROR_CODE)
        }catch (e : Exception){
            CommuHomeUseCaseResponse.CommuHomeDomain(false, ResponseCodeConstants.UNDEFINED_ERROR_CODE)
        }
    }

}