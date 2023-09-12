package com.nbmlon.mushroomer.api.dto

import com.google.gson.annotations.SerializedName

sealed class ReportResponseDTO {
    data class SuccessResponseDTO(
        @SerializedName("isSuccess") val success: Boolean,
        val code: Int = -1,
        val message: String = ""
    ) : ReportResponseDTO()
}