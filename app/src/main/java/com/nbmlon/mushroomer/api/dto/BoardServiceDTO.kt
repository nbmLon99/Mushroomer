package com.nbmlon.mushroomer.api.dto

import com.google.gson.annotations.SerializedName
import com.nbmlon.mushroomer.model.Comment
import com.nbmlon.mushroomer.model.Post
import com.nbmlon.mushroomer.model.ThumbsUp
import com.nbmlon.mushroomer.model.User
import com.nbmlon.mushroomer.ui.commu.board.BoardType
import org.joda.time.DateTime

sealed class BoardResponseDTO{
    data class PostsArrayResponseDTO(
        @SerializedName("data") val posts: ArrayList<PostResponseDTO>,
        @SerializedName("message") val message: String?
    ) : BoardResponseDTO()

    data class SuccessResponseDTO(
        @SerializedName("isSuccess") val success: Boolean,
        val code: Int = -1,
        val message: String = ""
    ) : BoardResponseDTO()


    data class PostResponseDTO(
        @SerializedName("createdDate") val createdDate: String,
        @SerializedName("updatedDate") val updatedDate: String,
        @SerializedName("boardId") val boardId: Int,
        @SerializedName("type") val type: String,
        @SerializedName("title") val title: String,
        @SerializedName("content") val content: String,
        @SerializedName("status") val status: String,
        @SerializedName("image") val images: List<String>,
        @SerializedName("comments") val comments: List<Comment>,
        @SerializedName("thumbsUps") val thumbsUps: List<ThumbsUp>
    ) : BoardResponseDTO(){
        fun toPost() : Post = Post(
            id = boardId,
            title = title,
            images = ArrayList(images),
            content = content,
            time = DateTime.parse(updatedDate),
            writer = User(
                id = 0,
                email = "",
                nickname = "",
                icon = "",
                name = "",
                phone_number = ""
            ),
            comments = null,
            ThumbsUpCount = 0,
            boardType = BoardType.values().find { it.serverName == type } ?: BoardType.FreeBoard,
            myThumbsUp = false,
            updated = updatedDate != createdDate
        )
    }
}
