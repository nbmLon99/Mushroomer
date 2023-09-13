package com.nbmlon.mushroomer.domain

import com.nbmlon.mushroomer.api.ResponseCodeConstants.UNDEFINED_ERROR_CODE
import com.nbmlon.mushroomer.api.dto.BoardResponseDTO
import com.nbmlon.mushroomer.model.Post

sealed class CommuWriteUseCaseRequest{
    data class UploadPostDomain(val post : Post) : CommuWriteUseCaseRequest()
    data class ModifiyPostDomain(val id : Int, val modified : Post) : CommuWriteUseCaseRequest()
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