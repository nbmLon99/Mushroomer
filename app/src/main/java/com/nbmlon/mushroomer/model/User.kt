package com.nbmlon.mushroomer.model


/**
 * @param userIdx           user 구분자
 * @param profileImage      프로필 이미지
 * @param name              이름
 * @param nickname          닉네임
 * @param email             이메일
 */
data class User(
    val userIdx : Long,
    val profileImage : String,
    val name : String,
    val nickname : String,
    val email : String,
)