package com.nbmlon.mushroomer.api.dto

import com.google.gson.annotations.SerializedName
import com.nbmlon.mushroomer.model.User

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name : String,
    val email : String,
    val password : String,
    val nickname : String,
    val cellphone : String
)

data class FindIdRequest(
    val name : String,
    val cellphone: String
)

data class FindPwRequest(
    val name : String,
    val email : String
)

data class LoginResponse(
    val success: Boolean = false,
    val refreshToken : String? = null,
    val token : String? = null,
    val loginUser : User? = null,
    val percentage : Int? = null
){

}

data class UserResponse(
    @SerializedName("") val items : User,
    @SerializedName("tokens") val token : String,
    @SerializedName("isSuccess") val isSuccess : Boolean
)

