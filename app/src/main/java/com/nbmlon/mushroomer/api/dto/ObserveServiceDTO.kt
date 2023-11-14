package com.nbmlon.mushroomer.api.dto

sealed class ObserveRequestDTO{
    data class ObserveDTO(
        val mushId : Int,
        val lng : Float?,
        val lat : Float?
    )
}