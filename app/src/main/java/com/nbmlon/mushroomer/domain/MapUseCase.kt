package com.nbmlon.mushroomer.domain

import com.nbmlon.mushroomer.api.ResponseCodeConstants
import com.nbmlon.mushroomer.model.MushHistory

sealed class MapUseCaseResponse{
    abstract val success : Boolean
    abstract val code : Int
    data class MapResponseDomain(
        override val success: Boolean = false,
        override val code : Int = ResponseCodeConstants.UNDEFINED_ERROR_CODE,
        val history : ArrayList<MushHistory>? = null
    ) : MapUseCaseResponse()
}