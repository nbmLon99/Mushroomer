package com.nbmlon.mushroomer.domain

import android.net.Uri
import com.nbmlon.mushroomer.api.ResponseCodeConstants.UNDEFINED_ERROR_CODE
import com.nbmlon.mushroomer.api.dto.BoardResponseDTO
import com.nbmlon.mushroomer.model.Post
import okhttp3.MultipartBody

sealed class CommuWriteUseCaseRequest{
    data class UploadPostDomain(val post : Post , val uploadImages : List<MultipartBody.Part>) : CommuWriteUseCaseRequest()
    data class ModifiyPostDomain(val id : Int, val modified : Post , val uploadImages : List<MultipartBody.Part>) : CommuWriteUseCaseRequest()
}


sealed class CommuWriteUseCaseResponse{
    abstract val success : Boolean
    abstract val code : Int
    data class SuccessResponseDomain(
        override val success : Boolean,
        override val code: Int = UNDEFINED_ERROR_CODE,
        val message : String = ""
        ) : CommuWriteUseCaseResponse()
}

fun BoardResponseDTO.SuccessResponseDTO.toWriteDomain() : CommuWriteUseCaseResponse =
    CommuWriteUseCaseResponse.SuccessResponseDomain(
        success = success,
        code = code,
        message  = message
    )