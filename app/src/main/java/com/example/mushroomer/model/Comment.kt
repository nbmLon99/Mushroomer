package com.example.mushroomer.model

import java.util.Date


/**
 * @param writer        작성자
 * @param content       댓글 내용
 * @param time          작성시간
 * @param writer        작성자
 * @param replies       답글 리스트 ( 코멘트 )
 */
data class Comment(
    val writer : User,
    val content : String,
    val time : Date,
    val replies : ArrayList<Comment>?
)
