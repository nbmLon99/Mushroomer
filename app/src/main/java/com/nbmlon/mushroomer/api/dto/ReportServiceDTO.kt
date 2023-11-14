package com.nbmlon.mushroomer.api.dto

import com.google.gson.annotations.SerializedName

sealed class ReportRequestDTO {
    data class ReportDTO(
        @SerializedName("reportType") val type : String,
        @SerializedName("reportCode") val code : Int,
        ) : ReportRequestDTO()

}