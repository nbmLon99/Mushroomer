package com.nbmlon.mushroomer.model

import org.joda.time.DateTime

enum class PostType{
    POST_PICTURE,
    POST_TEXT
}


/**
 * @param title         포스팅 제목
 * @param images        포스팅에 들어갈 이미지 url 순서대로
 * @param content       포스팅 내용
 * @param time          최종 작성시간
 * @param writer        작성자
 * @param comments      댓글
 * @param ThumbsUpCount 좋아요 수
 * 
 * @param myThumbsUp    내 좋아요 유무
 * @param updated       수정 유무 ( 수정됨 )
 */
data class Post(
    val title: String,
    val images: ArrayList<String>?,
    val content: String,
    val time: DateTime,
    val writer: User,
    val comments: ArrayList<Comment>,
    val ThumbsUpCount: Int,

    val myThumbsUp: Boolean,
    val updated: Boolean
    ) {
    companion object {
        fun getDummy(): Post {
            return Post(
                title = "제목",
                images = null,
                content = "내용",
                time = DateTime(),
                writer = User.getDummy(),
                comments = arrayListOf(),
                ThumbsUpCount = 0,
                myThumbsUp = false,
                updated = false
            )
        }

        fun getDummys() : ArrayList<Post>{
            val items = arrayListOf<Post>()
            for ( i in 1..10) {
                items.add(Post.getDummy())
            }
            return items
        }
    }
}