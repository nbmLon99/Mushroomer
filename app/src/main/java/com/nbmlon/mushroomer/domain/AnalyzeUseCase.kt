package com.nbmlon.mushroomer.domain

import android.graphics.Bitmap
import com.nbmlon.mushroomer.api.ResponseCodeConstants
import com.nbmlon.mushroomer.api.dto.MushroomResponseDTO
import com.nbmlon.mushroomer.model.AnalyzeResult
import com.nbmlon.mushroomer.model.MushHistory
import com.nbmlon.mushroomer.model.Mushroom


sealed class AnalyzeUseCaseRequest{
    data class AnalyzeRequestDomain(
        val pictures : ArrayList<Bitmap>
    ) : AnalyzeUseCaseRequest()

    data class SaveHistoryDomain(
        val mushHistory: MushHistory
    ) : AnalyzeUseCaseRequest()
}

/** 분석에 대한 마무리 response 값은 항상 SuccessResponseDomain으로 반환됨.
 * (결과값 성공) -> (AnalyzeResponseDomain으로 반환) -> (서버에 History 저장 요청 후 결과값 SuccessResponseDomain으로 반환)
 * (결과값 실패) -> (SuccessResponseDomain으로 반환)
 * **/
sealed class AnalyzeUseCaseResponse{
    abstract val success : Boolean
    abstract val code : Int
    data class AnalyzeResponseDomain(
        override val success: Boolean = false,
        override val code : Int = ResponseCodeConstants.UNDEFINED_ERROR_CODE,
        val mushroom : Mushroom,
        var isNew : Boolean = false,
        var accuracy : Int = -1
    ) :AnalyzeUseCaseResponse()
    data class SuccessResponseDomain(
        override val success: Boolean = false,
        override val code : Int = ResponseCodeConstants.UNDEFINED_ERROR_CODE
    ) :AnalyzeUseCaseResponse()
}

fun MushroomResponseDTO.MushResponseDTO.toAnalyzeDomain() : AnalyzeUseCaseResponse =
    AnalyzeUseCaseResponse.AnalyzeResponseDomain(
        success = success,
        mushroom = TODO("명세서 아직임")
    )

fun AnalyzeUseCaseResponse.AnalyzeResponseDomain.toResultModel() : AnalyzeResult =
    AnalyzeResult(
        mushroom = mushroom, isNew = isNew, accuracy = accuracy
    )