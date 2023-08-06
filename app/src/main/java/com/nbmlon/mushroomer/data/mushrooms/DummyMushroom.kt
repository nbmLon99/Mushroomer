package com.nbmlon.mushroomer.data.mushrooms

import com.nbmlon.mushroomer.model.MushType
import com.nbmlon.mushroomer.model.Mushroom

class DummyMushroom {
    fun getMush(n : Int) : Mushroom = Mushroom(n,"짱버섯","설명입니다.", MushType.EDIBLE,20L,ArrayList())

    fun getDogamPaging(pagNo : Int){
        val array = ArrayList<Mushroom>()
        for(i in 1..20){
            array.add(getMush(i))
        }

    }
}