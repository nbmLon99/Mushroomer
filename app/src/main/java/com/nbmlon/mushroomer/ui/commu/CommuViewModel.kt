package com.nbmlon.mushroomer.ui.commu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.nbmlon.mushroomer.data.posts.PostsRepository
import com.nbmlon.mushroomer.model.Comment
import com.nbmlon.mushroomer.model.Commu
import com.nbmlon.mushroomer.model.Post
import com.nbmlon.mushroomer.model.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommuViewModel(
    private val repository : PostsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _recentPosts = MutableLiveData<Commu>()
    val recentPostsForDisplay: LiveData<Commu> = _recentPosts


    //요청 후 take(1) 하여 반영
    val responseFlow = MutableSharedFlow<CommuResponse>()
    val request : (CommuRequest) -> Unit
    init{
        request = { commuRequest ->
            viewModelScope.launch{
                when( commuRequest ) {
                    is CommuRequest.ForReport -> {
                        val post = commuRequest.post
                        val comment = commuRequest.comment
                        // ForReport 처리 로직
                        responseFlow.emit(CommuResponse.ForReport(code = ResponseCode.SUCCESS.code))
                    }
                    is CommuRequest.ForDelete -> {
                        val post = commuRequest.post
                        val comment = commuRequest.comment
                        // ForDelete 처리 로직
                        responseFlow.emit(CommuResponse.ForDelete(code = ResponseCode.SUCCESS.code))
                    }
                    is CommuRequest.ForUpload -> {
                        val post = commuRequest.post
                        val comment = commuRequest.comment
                        // ForUpload 처리 로직
                        responseFlow.emit(CommuResponse.ForUpload(code = ResponseCode.SUCCESS.code))
                    }
                    is CommuRequest.ForModify -> {
                        val post = commuRequest.post
                        val comment = commuRequest.comment
                        // ForModify 처리 로직
                        responseFlow.emit(CommuResponse.ForModify(code = ResponseCode.SUCCESS.code))
                    }
                }
            }
        }
    }



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

    /** 페이징 데이터 요청 **/
    private fun loadPostsPaging(query : String?, boardType: BoardType): Flow<PagingData<Post>> =
        repository.getPostStream(query, boardType)


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

enum class ResponseCode(val code : Int){
    SUCCESS(200),
    FAIL(400)
}

sealed class CommuRequest{
    data class ForReport(val post : Post?, val comment : Comment?) : CommuRequest()
    data class ForDelete(val post : Post?, val comment : Comment?) : CommuRequest()
    data class ForUpload(val post : Post?, val comment : Comment?) : CommuRequest()
    data class ForModify(val post : Post?, val comment : Comment?) : CommuRequest()
}


sealed class CommuResponse{
    data class ForReport(val code : Int) : CommuResponse()
    data class ForDelete(val code : Int) : CommuResponse()
    data class ForUpload(val code : Int) : CommuResponse()
    data class ForModify(val code : Int) : CommuResponse()
}
