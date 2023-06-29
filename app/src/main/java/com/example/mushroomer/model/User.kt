package com.example.mushroomer.model

import java.util.Date

//pw는 서버단에서만 검증하는게 맞아 보여서 제외함.
data class `User`(
    val userIdx : Long,
    val nickname : String,
    val email : String,
    val name : String,
    val createdAt : Date,
    val updatedAt : Date,
    val status : String
)