package com.nbmlon.mushroomer.api.dto

import com.nbmlon.mushroomer.model.User


sealed class UserRequestDTO{
    data class LoginRequestDTO(
        val email: String,
        val password: String
    ): UserRequestDTO()

    data class TokenLoginRequestDTO(
        val token : String
    ) : UserRequestDTO()

    data class RegisterRequestDTO(
        val name : String,
        val email : String,
        val password : String,
        val nickname : String,
        val cellphone : String
    ): UserRequestDTO()

    data class FindIdRequestDTO(
        val name : String,
        val cellphone: String
    ): UserRequestDTO()

    data class FindPwRequestDTO(
        val name : String,
        val email : String
    ) : UserRequestDTO()
}



sealed class UserResponseDTO{
    data class LoginResponseDTO(
        val success: Boolean = false,
        val refreshToken : String? = null,
        val token : String? = null,
        val loginUser : User? = null,
        val percentage : Int? = null
    ) : UserResponseDTO()

    data class FindResponseDTO(
        val success: Boolean = false,
        val hint : String = ""
    ) : UserResponseDTO()

    data class SuccessResponse(
        val success: Boolean,
        val code: Int = -1,
        val message: String = ""
    ) : UserResponseDTO()
}

