package com.nbmlon.mushroomer.domain

import com.nbmlon.mushroomer.api.ResponseCodeConstants
import com.nbmlon.mushroomer.api.dto.UserRequestDTO
import com.nbmlon.mushroomer.api.dto.UserResponseDTO
import com.nbmlon.mushroomer.model.User

sealed class ProfileUseCaseRequest{
    data class WithdrawalRequestDomain(val email : String, val password : String) : ProfileUseCaseRequest()
    data class ModifyProfileRequestDomain(val password: String, val modifiedPwd : String? , val modified : User) : ProfileUseCaseRequest(){
        fun toDTO() : UserRequestDTO.RegisterRequestDTO =
            UserRequestDTO.RegisterRequestDTO(
                name = modified.name,
                email = modified.email,
                password = modifiedPwd ?: password,
                nickname = modified.nickname,
                cellphone = modified.phone_number
            )
    }
}


sealed class ProfileUseCaseResponse{
    abstract val success : Boolean
    abstract val code : Int
    data class SuccessResponseDomain(
        override val success: Boolean = false,
        override val code : Int = ResponseCodeConstants.UNDEFINED_ERROR_CODE
    ) : ProfileUseCaseResponse()
}


fun UserResponseDTO.WithdrawalResponseDTO.toProfileDomain() : ProfileUseCaseResponse =
    ProfileUseCaseResponse.SuccessResponseDomain(
        success = success,
        code = code
    )