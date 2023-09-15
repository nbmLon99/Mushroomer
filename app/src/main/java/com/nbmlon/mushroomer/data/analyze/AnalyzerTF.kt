package com.nbmlon.mushroomer.data.analyze

import android.graphics.Bitmap
import com.nbmlon.mushroomer.domain.AnalyzeResponse
import com.nbmlon.mushroomer.api.service.MushroomService
import com.nbmlon.mushroomer.domain.AnalyzeUseCaseRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await
import javax.inject.Inject

internal fun AnalyzerTF(domain : AnalyzeUseCaseRequest.AnalyzeRequestDomain) : ResponseTF{
    val pictures = domain
    return ResponseTF(mushId = 0, accuracy = 0)
}


data class ResponseTF(
    val mushId : Int,
    val accuracy : Int
)