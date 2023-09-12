package com.nbmlon.mushroomer.ui.commu.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbmlon.mushroomer.domain.CommuPostUseCaseRequest
import com.nbmlon.mushroomer.domain.CommuPostUseCaseRequest.*
import com.nbmlon.mushroomer.domain.CommuPostUseCaseResponse
import com.nbmlon.mushroomer.domain.CommuPostUseCaseResponse.*
import com.nbmlon.mushroomer.data.posts.CommuPostRepository
import com.nbmlon.mushroomer.model.Comment
import com.nbmlon.mushroomer.model.Post
import kotlinx.coroutines.launch

class ViewModelPost : ViewModel() {
    private val repository = CommuPostRepository()

    val _mPost = MutableLiveData<Post>()
    val mPost : LiveData<Post> = _mPost!!

    val _response = MutableLiveData<CommuPostUseCaseResponse>()
    val response : LiveData<CommuPostUseCaseResponse> = _response

    val request : (CommuPostUseCaseRequest) -> Unit

    init{
        request = { request ->
            viewModelScope.launch{
                when( request ) {
                    //신고
                    is ReportRequestDomain -> {
                        _response.value =  repository.report(request)
                    }
                    //삭제
                    is DeleteRequestDomain -> {
                        _response.value =  repository.delete(request)
                    }
                    //댓글등록
                    is UploadCommentRequestDomain -> {
                        _response.value =  repository.uploadComment(request)
                    }
                    //댓글수정
                    is ModifyCommentRequestDomain -> {
                        _response.value =  repository.modifyComment(request)
                    }
                    is LoadPostRequestDomain ->{
                        _mPost.value =  repository.loadPost(LoadPostRequestDomain(request.id)).post
                    }
                }
            }
        }
    }
}

