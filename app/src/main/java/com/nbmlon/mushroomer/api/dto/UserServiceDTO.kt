package com.nbmlon.mushroomer.api.dto

import com.google.gson.annotations.SerializedName
import com.nbmlon.mushroomer.api.dto.UserResponseDTO.*
import com.nbmlon.mushroomer.model.User

sealed class UserRequestDTO{
    data class LoginRequestDTO(
        val email : String,
        val password : String
    ) : UserRequestDTO()

    data class RegisterRequestDTO(
        val name : String,
        val email : String,
        val password : String,
        val nickname : String,
        val cellphone : String
    ) : UserRequestDTO()

    data class EditRequestDTO(
        val name : String,
        val password : String,
        val nickname : String,
        val cellphone : String,
        val imageUrl : String
    )

    data class WithdrawalRequestDTO(
        val email : String,
        val password: String
    )
}

sealed class UserResponseDTO{
    data class LoginResponseDTO(
        @SerializedName("isSuccess") val success: Boolean = false,
        val refreshToken : String? = null,
        val token : String? = null,
        val loginUser : User? = null,
        val percentage : Int? = null
    ) : UserResponseDTO()

    data class GenerateTokenResponseDTO(
        @SerializedName("isSuccess") val success: Boolean,
        val code: Int = -1,
        val message: String = ""
    ) : UserResponseDTO()

    data class KakaoLoginResponseDTO(
        @SerializedName("isSuccess") val success: Boolean = false,
        val refreshToken : String? = null,
        val token : String? = null,
        val loginUser : User? = null,
        val percentage : Int? = null
    ) : UserResponseDTO()

    data class ModifyUserResponseDTO(
        @SerializedName("isSuccess") val success: Boolean,
        val code: Int = -1,
        val message: String = ""
    ) : UserResponseDTO()

    data class WithdrawalResponseDTO(
        @SerializedName("isSuccess") val success: Boolean,
        val code: Int = -1,
        val message: String = ""
    ) : UserResponseDTO()

    data class SignUpResponseDTO(
        @SerializedName("isSuccess") val success: Boolean,
        val code: Int = -1,
        val message: String = ""
    ) : UserResponseDTO()
}


