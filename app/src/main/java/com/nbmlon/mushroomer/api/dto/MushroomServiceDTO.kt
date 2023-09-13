package com.nbmlon.mushroomer.api.dto

import com.google.gson.annotations.SerializedName

sealed class MushroomResponseDTO {
    data class MushesResponseDTO(
        @SerializedName("isSuccess") val success: Boolean,
        val code: Int = -1,
        val message: String = ""
    ) : MushroomResponseDTO()
    data class MushResponseDTO(
        @SerializedName("isSuccess") val success: Boolean,
        val code: Int = -1,
        val message: String = ""
    ) : MushroomResponseDTO()
}
