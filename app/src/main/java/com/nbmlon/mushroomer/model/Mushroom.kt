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
    companion object {
        /** n(도감넘버), gotcha(발견 여부) 지정하여 더미데이터 생성 **/
        fun getDummy(n :Int, gotcha : Boolean) : Mushroom{
            val mush = Mushroom(n,"","${n}번쨰 버섯","설명입니다.", MushType.EDIBLE,20L,ArrayList())
            if(gotcha){ mush.myPicPath.add("example") }
            return mush
        }
    }
    val gotcha : Boolean get() =  myPicPath.size > 0
}

