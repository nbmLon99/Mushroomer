package com.nbmlon.mushroomer.domain

import com.nbmlon.mushroomer.api.ResponseCodeConstants.UNDEFINED_ERROR_CODE
import com.nbmlon.mushroomer.model.Comment
import com.nbmlon.mushroomer.model.Post

sealed class CommuPostUseCaseRequest{
    data class ReportRequestDomain(val articleId : Int, val type : TargetType, val id : Int) : CommuPostUseCaseRequest()
    data class DeleteRequestDomain( val type : TargetType,val articleId : Int, val commentId : Int? = null) : CommuPostUseCaseRequest()
    data class UploadCommentRequestDomain(val articleId : Int, val target : Comment) : CommuPostUseCaseRequest()
    data class ModifyCommentRequestDomain(val articleId : Int, val target : Comment, val modified : String) : CommuPostUseCaseRequest()
    data class LoadPostRequestDomain(val id : Int) :  CommuPostUseCaseRequest()
    data class ChangeThumbsUpRequestDomain(val toLike : Boolean, val articleId : Int) : CommuPostUseCaseRequest()

}

sealed class CommuPostUseCaseResponse{
    abstract val success : Boolean
    abstract val code : Int
    data class SuccessResponseDomain(
        override val success: Boolean = false,
        override val code : Int = UNDEFINED_ERROR_CODE,
        val message: String = ""
    ) : CommuPostUseCaseResponse()
    data class PostResponseDomain(
        override val success: Boolean = false,
        override val code : Int = UNDEFINED_ERROR_CODE,
        val post : Post? = null
    ) : CommuPostUseCaseResponse()
    data class PostsResponseDomain(
        override val success: Boolean = false,
        override val code : Int = UNDEFINED_ERROR_CODE,
        val posts : List<Post> = listOf()
    ) : CommuPostUseCaseResponse()
}
enum class TargetType { POST, COMMENT }