package com.example.mushroomer.model

import java.util.Date

const val UNDEFINED : Long =  -1L;


/*
--------서버 단에서 계산하여 받을 필요 있는 멤버변수 ------
 좋아요 수(ThumbsUpCount)
 내 좋아요 체크 여부 (myThumbsUp)
 */
data class Post(
    val boardIdx : Long,
    val userIdx : Long,
    val title : String,
    val content : String,
    val createdAt : Date,
    val updatedAt : Date,
    val status : String,
    val fileUrl : String?,

    val ThumbsUpCount : Int,
    val myThumsUp : Boolean
    )