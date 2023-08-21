package com.nbmlon.mushroomer.ui.commu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nbmlon.mushroomer.model.Commu
import com.nbmlon.mushroomer.model.Post
import com.nbmlon.mushroomer.model.PostType
import com.nbmlon.mushroomer.ui.login.LoginFormState
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
                newQnAPosts = Post.getDummys(PostType.POST_TEXT),
                newFreePosts =  Post.getDummys(PostType.POST_TEXT),
                newPicPosts = Post.getDummys(PostType.POST_PHOTO)
            )
        }
    }







}