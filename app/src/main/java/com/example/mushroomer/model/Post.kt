package com.example.mushroomer.model

import java.util.Date



/**
 * @param title         포스팅 제목
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
    val title : String,
    val content : ArrayList<String>,
    val time : Date,
    val writer : User,
    val comments : ArrayList<Comment>,
    val ThumbsUpCount : Int,

    val myThumbsUp : Boolean,
    val updated : Boolean
    )