package com.nbmlon.mushroomer.model

import android.graphics.Bitmap

class Analyze{
    data class AnalyzeResponse(
        val accuracy : Int,
        val mushroom : Mushroom
    ){
        val isNew = !mushroom.gotcha
    }
    data class AnalyzeRequest(
        val pictures : ArrayList<Bitmap>
    )
}
