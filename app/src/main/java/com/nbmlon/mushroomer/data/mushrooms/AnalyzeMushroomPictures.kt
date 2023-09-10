package com.nbmlon.mushroomer.data.mushrooms

import android.graphics.Bitmap
import com.nbmlon.mushroomer.api.dto.AnalyzeResponse
import com.nbmlon.mushroomer.api.service.MushroomService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await
import javax.inject.Inject

class AnalyzeMushroomPictures(private val pictures: ArrayList<Bitmap>){
    @Inject
    private lateinit var backend : MushroomService


    /** Lite Tensorflow **/
    suspend fun analyzeMushroomPictures() : AnalyzeResponse {

        //pictures-> acccuracy, mushId
        val mushId = 112
        val accuracy = 80

        //버섯 정보 가져오기
        val mush = withContext(Dispatchers.IO){
            backend.getMushroom(mushId)
        }


        val response = AnalyzeResponse(
            mushroom = mush.await()
        )

        //가져온 정보에 정확도 부여
        response.accuracy = accuracy
        return response
    }
}
