package com.nbmlon.mushroomer.api.dto

import com.google.gson.annotations.SerializedName

sealed class CommentResponseDTO{
    data class SuccessResponseDTO(
        @SerializedName("isSuccess") val success: Boolean,
        val code: Int = -1,
        val message: String = ""
    ) : BoardResponseDTO()
}
