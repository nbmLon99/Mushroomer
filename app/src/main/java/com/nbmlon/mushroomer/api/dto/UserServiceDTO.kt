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
    data class EditNicknameDTO(
        @SerializedName("nickname") val nickname: String
    )
    data class EditIconDTO(
        @SerializedName("imageUrl") val imageUrl : String
    )

    data class WithdrawalRequestDTO(
        val email : String,
        val password: String
    )

    data class TargetEmailDTO(
        val email : String
    )

    data class TargetPhoneDTO(
        val cellphone : String
    )
}

sealed class UserResponseDTO{
    data class LoginResponseDTO(
        @SerializedName("userId") val id : Int,
        @SerializedName("name") val name: String,
        @SerializedName("nickname") val nickname: String,
        @SerializedName("cellphone") val phone : String,
        @SerializedName("email") val email : String,
        @SerializedName("image") val imageUrl: String,
        @SerializedName("accessToken") val accessToken : String,
        @SerializedName("refreshToken") val refreshToken : String,

        @SerializedName("message") val message: String,
        val percentage : Int? = null
    ) : UserResponseDTO()

    data class GenerateTokenResponseDTO(
         @SerializedName("data") val code: Int = -1,
        val message: String = ""
    ) : UserResponseDTO()

    data class AuthCodeResponseDTO(
        @SerializedName("authcode") val authCode : String
    ) : UserResponseDTO()

    data class FoundPasswordDTO(
        val password : String
    ) : UserResponseDTO()

    data class FoundIdDTO(
        val userEmail : String
    ) : UserResponseDTO()
}


