package com.nbmlon.mushroomer.domain

import com.nbmlon.mushroomer.api.dto.BoardResponseDTO
import com.nbmlon.mushroomer.api.dto.CommentResponseDTO
import com.nbmlon.mushroomer.api.dto.ReportResponseDTO
import com.nbmlon.mushroomer.model.Comment
import com.nbmlon.mushroomer.model.Post

sealed class CommuPostUseCaseRequest{
    data class ReportRequestDomain(val type : TargetType, val id : Int) : CommuPostUseCaseRequest()
    data class DeleteRequestDomain(val type : TargetType, val id : Int) : CommuPostUseCaseRequest()
    data class UploadCommentRequestDomain(val target : Comment) : CommuPostUseCaseRequest()
    data class ModifyCommentRequestDomain(val target : Comment, val modified : String) : CommuPostUseCaseRequest()
    data class LoadPostRequestDomain(val id : Int) :  CommuPostUseCaseRequest()

}

sealed class CommuPostUseCaseResponse{
    abstract val success : Boolean
    abstract val code : Int
    data class SuccessResponseDomain(
        override val success: Boolean = false,
        override val code : Int = -1,
        val message: String = ""
    ) : CommuPostUseCaseResponse()
    data class PostResponseDomain(
        override val success: Boolean = false,
        override val code : Int = -1,
        val post : Post? = null
    ) : CommuPostUseCaseResponse()
}

// 굳이 필없을듯?
fun BoardResponseDTO.PostResponseDTO.toPostDomain() : CommuPostUseCaseResponse.PostResponseDomain =
    CommuPostUseCaseResponse.PostResponseDomain(
        success = true,
        post = this.toPost()
    )

fun ReportResponseDTO.SuccessResponseDTO.toPostDomain() : CommuPostUseCaseResponse =
    CommuPostUseCaseResponse.SuccessResponseDomain(
        success = success,
        code = code,
        message = message
    )


fun BoardResponseDTO.SuccessResponseDTO.toPostDomain() : CommuPostUseCaseResponse =
    CommuPostUseCaseResponse.SuccessResponseDomain(
        success = success, code = code, message = message
    )


fun CommentResponseDTO.SuccessResponseDTO.toPostDomain() : CommuPostUseCaseResponse =
    CommuPostUseCaseResponse.SuccessResponseDomain(
        success= success, code= code, message= message
    )


enum class TargetType { POST, COMMENT }