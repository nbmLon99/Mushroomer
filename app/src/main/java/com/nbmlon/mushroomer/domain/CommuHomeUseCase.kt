package com.nbmlon.mushroomer.domain

import com.nbmlon.mushroomer.api.ResponseCodeConstants.UNDEFINED_ERROR_CODE
import com.nbmlon.mushroomer.model.Post


sealed class CommuHomeUseCaseResponse{
    abstract val success : Boolean
    abstract val code : Int
    data class CommuHomeDomain(
        override val success: Boolean = false,
        override val code: Int = UNDEFINED_ERROR_CODE,
        val freeBoardPosts : ArrayList<Post>? = null,
        val qnaBoardPosts : ArrayList<Post>? = null,
        val picBoardPosts : ArrayList<Post>? = null,
    ) : CommuHomeUseCaseResponse()
}