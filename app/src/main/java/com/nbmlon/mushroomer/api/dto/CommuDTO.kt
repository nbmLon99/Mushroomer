package com.nbmlon.mushroomer.api.dto

sealed class CommuPostRequestDTO{
    data class ReportDTO(val id : Int) : CommuPostRequestDTO()
    data class DeleteDTO(val id : Int) : CommuPostRequestDTO()
    data class UploadDTO(val id : Int, val target : Any?) : CommuPostRequestDTO()
    data class ModifyDTO(val id : Int, val target : Any?) : CommuPostRequestDTO()
}

sealed class CommuPostResponseDTO{
    data class SuccessResponseDTO(
        val success : Boolean,
        val code: Int = -1,
        val message: String = ""
    ) : CommuPostResponseDTO()
}