package com.nbmlon.mushroomer.domain

import com.nbmlon.mushroomer.model.Post

sealed class CommuWriteRequestDTO{
    data class UploadPostDTO(val post : Post) : CommuWriteRequestDTO()
    data class ModifiyPostDTO(val id : Int, val modified : Post) : CommuWriteRequestDTO()
}


sealed class CommuWriteResponseDTO{
    abstract val success : Boolean
    abstract val code : Int
    data class SuccessResponse(
        override val success : Boolean,
        override val code: Int
        ) : CommuWriteResponseDTO()
}