package com.nbmlon.mushroomer.ui.commu.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbmlon.mushroomer.api.dto.CommuPostRequestDTO
import com.nbmlon.mushroomer.api.dto.CommuPostRequestDTO.*
import com.nbmlon.mushroomer.api.dto.CommuPostResponseDTO
import com.nbmlon.mushroomer.api.dto.CommuPostResponseDTO.*
import com.nbmlon.mushroomer.data.posts.CommuPostRepository
import com.nbmlon.mushroomer.model.Comment
import com.nbmlon.mushroomer.model.Post
import kotlinx.coroutines.launch

class ViewModelPost : ViewModel() {
    fun loadTargetPost(id : Int) {
        viewModelScope.launch {
            _mPost.value =  repository.loadPost(LoadPostDTO(id))
        }
    }

    private val repository = CommuPostRepository()

    val _mPost = MutableLiveData<Post>()
    val mPost : LiveData<Post> = _mPost!!

    val _response = MutableLiveData<CommuPostResponse>()
    val response : LiveData<CommuPostResponse> = _response

    val request : (CommuPostRequest) -> Unit

    init{
        request = { request ->
            viewModelScope.launch{
                val dto = request.dto
                when( request ) {
                    is CommuPostRequest.ForReport -> {
                        _response.value =  repository.report(request.targetType, dto)
                    }
                    is CommuPostRequest.ForDelete -> {
                        _response.value =  repository.delete(request.targetType, dto)
                    }
                    is CommuPostRequest.ForUploadComment -> {
                        _response.value =  repository.uploadComment(dto)
                    }
                    is CommuPostRequest.ForModifyComment -> {
                        _response.value =  repository.modifyComment(dto)
                    }
                }
            }
        }
    }
}

enum class TargetType { POST, COMMENT }

sealed class CommuPostRequest{
    abstract val dto : CommuPostRequestDTO
    data class ForReport(val targetType: TargetType, override val dto : ReportDTO) : CommuPostRequest()
    data class ForDelete(val targetType: TargetType, override val dto : DeleteDTO) : CommuPostRequest()
    data class ForUploadComment(val replyFor : Comment?, override val dto : UploadCommentDTO) : CommuPostRequest()
    data class ForModifyComment(override val dto : ModifyCommentDTO) : CommuPostRequest()
}

sealed class CommuPostResponse{
    abstract val dto : CommuPostResponseDTO
    data class SuccessResponse(override val dto : SuccessResponseDTO) : CommuPostResponse()
    data class LoadedPostResponse(override val dto: PostDTO) : CommuPostResponse()
}