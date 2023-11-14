package com.nbmlon.mushroomer.api.dto

import com.google.gson.annotations.SerializedName

data class DefaultResponseDTO(
    @SerializedName("data") val code: Int = -1,
    val message: String = ""
)