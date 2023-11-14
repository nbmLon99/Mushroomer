package com.nbmlon.mushroomer.api.dto

import com.google.gson.annotations.SerializedName
import com.nbmlon.mushroomer.model.Post
import com.nbmlon.mushroomer.model.User
import com.nbmlon.mushroomer.ui.commu.board.BoardType
import org.joda.time.DateTime

sealed class BoardRequestDTO{
    data class PostRequestDTO(
        @SerializedName("title") val title : String,
        @SerializedName("content") val content : String,
        @SerializedName("type") val type : String
    ) : BoardRequestDTO()
}

sealed class BoardResponseDTO{
    // 게시판 조회
    data class PostsResponseDTO(
        @SerializedName("data") val posts: List<PostDTO>,
        @SerializedName("message") val message: String?
    ) : BoardResponseDTO()

    // 특정 게시글 조회
    data class PostResponseDTO(
        @SerializedName("data") val post: PostDTO,
        @SerializedName("message") val message: String?
    )

    data class PostDTO(
        @SerializedName("articleId") val articleId: Int,
        @SerializedName("type") val type: String,
        @SerializedName("title") val title: String,
        @SerializedName("content") val content: String,
        @SerializedName("status") val status: String,
        @SerializedName("image") val images: List<String>,
        @SerializedName("userId") val userId : Int,
        @SerializedName("nickname") val nickname : String,
        @SerializedName("likeCount") val likeCount : Int,
        @SerializedName("localDateTime") val time: String,
    ) : BoardResponseDTO(){
        fun toPost() : Post {
            val boardType = enumValues<BoardType>().firstOrNull { it.serverName == type } ?: BoardType.FreeBoard

            return Post(
                id = articleId,
                title = title,
                images = ArrayList(images),
                content = content,
                time = DateTime.parse(time),
                writer = User(
                    id = userId,
                    email = "",
                    nickname = nickname,
                    icon = "",
                    name = "",
                    phone_number = ""
                ),
                comments = arrayListOf(),
                ThumbsUpCount = likeCount,
                boardType = boardType,
                myThumbsUp = false,
                updated = false
            )
        }
    }
}
