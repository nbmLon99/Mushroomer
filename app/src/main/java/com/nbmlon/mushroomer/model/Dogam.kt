package com.nbmlon.mushroomer.model

/**
 * @param pageNo 도감 페이지 넘버
 */
data class Dogam(
    val pageNo : Int
){
    companion object{
        fun getDummy(pagNo : Int) : ArrayList<Mushroom>{
            val array = ArrayList<Mushroom>()
            for(i in 1..20){
                array.add(Mushroom.getDummy(i,i%3 == 0))
            }
            return array
        }
    }

}
