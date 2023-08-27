package com.nbmlon.mushroomer.model

/**
 * @param pageNo 도감 페이지 넘버
 */
data class Dogam(
    val pageNo : Int,
    val progress : Int
){
    companion object{
        fun getDummy(totalCount : Int, mushName : String? = null) : ArrayList<Mushroom>{
            val array = ArrayList<Mushroom>()
            for(i in 1..totalCount){
                array.add(Mushroom.getDummy(i,i%3 == 0, mushName))
            }
            return array
        }
    }

}
