package com.nbmlon.mushroomer.data.posts

import com.nbmlon.mushroomer.api.service.BoardService
import com.nbmlon.mushroomer.domain.CommuHomeUseCaseResponse
import javax.inject.Inject

/** 홈화면 저장소 **/
interface CommuHomeRepository {
    // 게시판 홈 화면에 띄울 최신글 가져옴
    fun getRecentPostsIntoHome() : CommuHomeUseCaseResponse.CommuHomeDomain
}


fun CommuHomeRepository() : CommuHomeRepository = CommuHomeRepositoryImpl()

private class CommuHomeRepositoryImpl : CommuHomeRepository {
    @Inject
    lateinit var service : BoardService
    override fun getRecentPostsIntoHome(): CommuHomeUseCaseResponse.CommuHomeDomain {
        TODO("API 명세서 필요")
    }

}