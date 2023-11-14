package com.nbmlon.mushroomer.domain

import com.nbmlon.mushroomer.api.ResponseCodeConstants
import com.nbmlon.mushroomer.api.dto.MushroomResponseDTO
import com.nbmlon.mushroomer.model.MushHistory
import com.nbmlon.mushroomer.model.Mushroom
import com.nbmlon.mushroomer.ui.dogam.DogamSortingOption

sealed class DogamUseCaseReqeust{
    data class LoadDogamResquestDomain(val query : String?, val sortingOption: DogamSortingOption)
    data class SpecificDogamRequestDomain(val id : Int)
}

sealed class DogamUseCaseResponse{
    abstract val success : Boolean
    abstract val code : Int
    data class LoadDogamResponse(
        override val success: Boolean = false,
        override val code : Int = ResponseCodeConstants.UNDEFINED_ERROR_CODE,
        val items: List<Mushroom>
    ) : DogamUseCaseResponse()
    data class SpecificDogamResponse(
        override val success: Boolean = false,
        override val code : Int = ResponseCodeConstants.UNDEFINED_ERROR_CODE,
        val mush: Mushroom? = null,
        val history : MushHistory? = null
    ) : DogamUseCaseResponse()
}