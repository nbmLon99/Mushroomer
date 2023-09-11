package com.nbmlon.mushroomer.api.dto

import com.google.gson.annotations.SerializedName
import com.nbmlon.mushroomer.model.Comment
import com.nbmlon.mushroomer.model.ThumbsUp
import com.nbmlon.mushroomer.ui.commu.post.CommuPostResponse

sealed class CommuPostRequestDTO{
    data class ReportDTO(val id : Int) : CommuPostRequestDTO()
    data class DeleteDTO(val id : Int) : CommuPostRequestDTO()
    data class UploadCommentDTO( val target : Comment) : CommuPostRequestDTO()
    data class ModifyCommentDTO( val target : Comment, val modified : String) : CommuPostRequestDTO()
    data class LoadPostDTO(val id : Int) :  CommuPostRequestDTO()

}

sealed class CommuPostResponseDTO{
    data class SuccessResponseDTO(
        val success : Boolean,
        val code: Int = -1,
        val message: String = ""
    ) : CommuPostResponseDTO()
    data class PostDTO(
        @SerializedName("createdDate") val createdDate: String,
        @SerializedName("updatedDate") val updatedDate: String,
        @SerializedName("boardId") val boardId: Int,
        @SerializedName("type") val type: String,
        @SerializedName("title") val title: String,
        @SerializedName("content") val content: String,
        @SerializedName("status") val status: String,
        @SerializedName("image") val image: String,
        @SerializedName("comments") val comments: List<Comment>, // 댓글 형식을 알고 있다면 해당 형식으로 변경
        @SerializedName("thumbsUps") val thumbsUps: List<ThumbsUp> // 좋아요 형식을 알고 있다면 해당 형식으로 변경
    ) : CommuPostResponseDTO()
}