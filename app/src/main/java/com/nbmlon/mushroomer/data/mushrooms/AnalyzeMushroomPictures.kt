package com.nbmlon.mushroomer.data.mushrooms

import android.graphics.Bitmap
import com.nbmlon.mushroomer.model.Analyze
import com.nbmlon.mushroomer.model.MushType
import com.nbmlon.mushroomer.model.Mushroom

class AnalyzeMushroomPictures(private val pictures: ArrayList<Bitmap>){

    //val repository : MushroomsRepository


    /** Lite Tensorflow **/
    fun analyzeMushroomPictures() : Analyze.AnalyzeResponse {
        val mushId = 112
        val accuracy = 80

        val request = Analyze.AnalyzeRequest(mushId = 112)
        val response = Analyze.AnalyzeResponse(
            mushroom = Mushroom(
                dogamNo = 112,
                imageUrl = "",
                name = "결과버섯",
                feature = "특징123123123\n\n\n\n\n\n\nn\n\n\nn\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "n\n" +
                        "\n" +
                        "\n\\n\n\n 이거야",
                type =MushType.POISON,
                rarity = 30,
                myHistory = arrayListOf()
            )
        )

        response.accuracy = accuracy
        //picture로 사진분석
        return response
    }
}

