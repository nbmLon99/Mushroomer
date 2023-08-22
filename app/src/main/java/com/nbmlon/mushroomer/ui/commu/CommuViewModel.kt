package com.nbmlon.mushroomer.ui.commu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nbmlon.mushroomer.model.Commu
import com.nbmlon.mushroomer.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class CommuViewModel : ViewModel() {
    private val _recentPosts = MutableLiveData<Commu>()
    val recentPostsForDisplay: LiveData<Commu> = _recentPosts

    suspend fun loadRecentPosts(){
        return withContext(Dispatchers.IO){
            delay(50000)
            _recentPosts.value = Commu(
                newQnAPosts = Post.getDummys(BoardType.QnABoard),
                newFreePosts =  Post.getDummys(BoardType.FreeBoard),
                newPicPosts = Post.getDummys(BoardType.PicBoard)
            )
        }
    }







}