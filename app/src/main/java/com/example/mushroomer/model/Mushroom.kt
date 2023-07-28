package com.example.mushroomer.model

enum class MushType{
    EDIBLE,
    POISON
}


/**
 * @param dogamNo   도감 넘버
 * @param name      버섯 이름
 * @param feature   특징
 * @param type      버섯 타입 ( 독버섯, 식용버섯 )
 * @param rarity    희귀도
 * @param myPicPath 나의 발견 사진 path (path에 날짜 저장 예정)
 */
data class Mushroom (
        val dogamNo : Int,
        val name : String,
        val feature : String,
        val type : Int,
        val rarity : Long,
        val myPicPath : ArrayList<String>
        )