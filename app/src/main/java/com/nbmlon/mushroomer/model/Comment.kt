package com.nbmlon.mushroomer.model

import org.joda.time.DateTime
import java.util.Date


/**
 * @param writer        작성자
 * @param content       댓글 내용
 * @param time          작성시간
 * @param writer        작성자
 * @param replies       답글 리스트 ( 코멘트 )
 * @param isReply       답글?
 */
data class Comment(
    val writer : User,
    val content : String,
    val time : DateTime,
    val replies : ArrayList<Comment>?,
    val isReply : Boolean
)
