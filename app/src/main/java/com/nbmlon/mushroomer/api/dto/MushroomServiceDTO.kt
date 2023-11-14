package com.nbmlon.mushroomer.api.dto

import com.google.gson.annotations.SerializedName

sealed class MushroomResponseDTO {
    data class MushesResponseDTO(
        val data : List<MushDTO>,
        val message: String = ""
    ) : MushroomResponseDTO()
    data class MushResponseDTO(
        val data : MushDTO,
        val message: String = ""
    ) : MushroomResponseDTO()

    data class MushDTO(
        @SerializedName("mushId") val id : Int,
        val name : String,
        val feature : String,
        val rarity : Int,
        @SerializedName("whichMush") val type : String,
        @SerializedName("image") val imageUrl : String,
        @SerializedName("isCatched") val gotcha : String
    )
}
