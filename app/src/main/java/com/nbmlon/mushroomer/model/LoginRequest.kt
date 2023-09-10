package com.nbmlon.mushroomer.model

data class RegisterRequest(
    val name : String,
    val email : String,
    val password : String,
    val nickname : String,
    val cellphone : String
)
