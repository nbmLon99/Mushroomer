package com.nbmlon.mushroomer.model

import retrofit2.http.Url

enum class MushType{
    EDIBLE,
    POISON
}


/**
 * @param dogamNo   도감 넘버
 * @param imageUrl  사진 url
 * @param name      버섯 이름
 * @param feature   특징
 * @param type      버섯 타입 ( 독버섯, 식용버섯 )
 * @param rarity    희귀도
 * @param myPicPath 나의 발견 사진 path (path에 날짜 저장 예정) -> 0개일 시 발견 못한 버섯으로 취급
 */
data class Mushroom (
        val dogamNo : Int,
        val imageUrl : String,
        val name : String,
        val feature : String,
        val type : MushType,
        val rarity : Long,
        val myPicPath : ArrayList<String>
        ){
    val gotcha : Boolean get() =  myPicPath.size > 0
}