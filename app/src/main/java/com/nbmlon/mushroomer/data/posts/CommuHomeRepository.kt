package com.nbmlon.mushroomer.data.posts

import com.nbmlon.mushroomer.model.Post
import com.nbmlon.mushroomer.ui.commu.board.BoardType

/** 홈화면 저장소 **/
interface CommuHomeRepository {
    // 게시판 홈 화면에 띄울 최신글 가져옴
    fun getRecentPostsIntoHome(boardType: BoardType) : ArrayList<Post>
}


fun CommuHomeRepository() : CommuHomeRepository = CommuHomeRepositoryImpl()

private class CommuHomeRepositoryImpl : CommuHomeRepository {
    override fun getRecentPostsIntoHome(boardType: BoardType): ArrayList<Post> {
        TODO("Not yet implemented")
    }

}