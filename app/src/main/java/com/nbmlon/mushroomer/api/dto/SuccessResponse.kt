package com.nbmlon.mushroomer.api.dto

data class SuccessResponse(
    val isSuccess: Boolean,
    val code: Int,
    val message: String
)