package com.nbmlon.mushroomer.api.dto

import com.google.gson.annotations.SerializedName

sealed class CommentRequestDTO{
    data class UploadCommentDTO(
        val content : String,
        val parentId : Int?
    ) : CommentRequestDTO()
}

sealed class CommentResponseDTO{
    data class CommentsDTO(
        val data : List<CommentDTO>,
        val message: String
    ) : CommentResponseDTO()
    data class CommentDTO(
        @SerializedName("commentId") val id : Int,
        @SerializedName("parentId") val parentId : Int,
        @SerializedName("content") val content : String
    ) : CommentResponseDTO()
}
