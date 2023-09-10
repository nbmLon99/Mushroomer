package com.nbmlon.mushroomer.ui.commu.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nbmlon.mushroomer.data.posts.CommuHomeRepository
import com.nbmlon.mushroomer.model.Commu
import com.nbmlon.mushroomer.model.Post
import com.nbmlon.mushroomer.ui.commu.board.BoardType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext


/**
 * 커뮤 홈 화면을 위한 뷰모델
 * **/
class ViewModelCommuHome(
) : ViewModel() {
    val repository = CommuHomeRepository()
    private val _recentPosts = MutableLiveData<Commu>()
    val recentPostsForDisplay: LiveData<Commu> = _recentPosts


    /** 홈화면에 띄울 최신 게시글 로드 **/
    suspend fun loadRecentPosts(){
        return withContext(Dispatchers.IO){
            delay(3000)

            val updatedValue = Commu(
                newQnAPosts = Post.getDummys(BoardType.QnABoard),
                newFreePosts =  Post.getDummys(BoardType.FreeBoard),
                newPicPosts = Post.getDummys(BoardType.PicBoard)
            )

            withContext(Dispatchers.Main) {
                _recentPosts.value = updatedValue
            }
        }
    }
}