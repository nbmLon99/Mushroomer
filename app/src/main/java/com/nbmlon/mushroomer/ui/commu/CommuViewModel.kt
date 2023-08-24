package com.nbmlon.mushroomer.ui.commu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nbmlon.mushroomer.model.Comment
import com.nbmlon.mushroomer.model.Commu
import com.nbmlon.mushroomer.model.Post
import com.nbmlon.mushroomer.model.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class CommuViewModel : ViewModel() {
    private val _recentPosts = MutableLiveData<Commu>()
    val recentPostsForDisplay: LiveData<Commu> = _recentPosts


    private val _responseModify = MutableLiveData<Response>()
    val responseModify: LiveData<Response> = _responseModify
    private val _responseDelete = MutableLiveData<Response>()
    val responseDelete: LiveData<Response> = _responseDelete
    private val _responseUpload = MutableLiveData<Response>()
    val responseUpload: LiveData<Response> = _responseUpload
    private val _responseReport = MutableLiveData<Response>()
    val responseReport: LiveData<Response> = _responseReport

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

    /** 서버에 수정 요청 **/
    suspend fun requestModify(targetPost : Post?, targetComment : Comment?){
        if (targetPost != null || targetComment == null)
            //Comment 수정
            return


        return withContext(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _responseModify.value = Response()
            }
        }
    }

    /** 서버에 등록 요청 **/
    suspend fun requestUpload(targetPost : Post?, targetComment : Comment?){
        if (targetPost != null || targetComment == null)
        //Comment 수정
            return


        return withContext(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _responseUpload.value = Response()
            }
        }
    }

    /** 서버에 삭제 요청 **/
    suspend fun requestDelete(targetPost : Post?, targetComment : Comment?){
        if (targetPost != null || targetComment == null)
        //Comment 수정
            return


        return withContext(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _responseDelete.value = Response()
            }
        }
    }


    /** 서버에 신고 접수 **/
    suspend fun requestReport(targetPost : Post?, targetComment : Comment?){
        if (targetPost != null || targetComment == null)
        //Comment 수정
            return


        return withContext(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _responseReport.value = Response()
            }
        }
    }

}