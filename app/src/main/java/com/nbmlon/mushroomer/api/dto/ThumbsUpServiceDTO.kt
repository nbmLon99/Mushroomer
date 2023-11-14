package com.nbmlon.mushroomer.api.dto

import com.google.gson.annotations.SerializedName

sealed class ThumbsUpRequestDTO{
    data class ThumbsUpDTO(
        val articleId : Int
    ) : ThumbsUpRequestDTO()
}