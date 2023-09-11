package com.nbmlon.mushroomer.api.dto

sealed class CommuRequestDTO{
    data class ReportDTO(val id : Int) : CommuRequestDTO()
    data class DeleteDTO(val id : Int) : CommuRequestDTO()
    data class UploadDTO(val id : Int, val target : Any?) : CommuRequestDTO()
    data class ModifyDTO(val id : Int, val target : Any?) : CommuRequestDTO()
}

sealed class CommuResponseDTO{
    data class SuccessResponseDTO(
        val success : Boolean,
        val code: Int = -1,
        val message: String = ""
    ) : CommuResponseDTO()
}