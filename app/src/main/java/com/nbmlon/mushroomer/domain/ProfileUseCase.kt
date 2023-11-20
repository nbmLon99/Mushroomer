package com.nbmlon.mushroomer.domain

import com.nbmlon.mushroomer.api.EndConverter.sha256
import com.nbmlon.mushroomer.api.ResponseCodeConstants
import com.nbmlon.mushroomer.api.dto.UserRequestDTO
import com.nbmlon.mushroomer.model.User
import okhttp3.MultipartBody

sealed class ProfileUseCaseRequest{
    data class WithdrawalRequestDomain(val email : String, val password : String) : ProfileUseCaseRequest(){
        fun toDTO() : UserRequestDTO.WithdrawalRequestDTO =
            UserRequestDTO.WithdrawalRequestDTO(
                email = email, password = password
            )
    }
    data class ModifyIconRequestDomain(val icon : MultipartBody.Part) : ProfileUseCaseRequest()
    data class ModifyNicknameRequestDomain(val nickname : String) : ProfileUseCaseRequest()
    data class ModifyPwdRequestDomain(val password: String, val modifiedPwd : String?, val modified : User) : ProfileUseCaseRequest(){
        fun toDTO() : UserRequestDTO.RegisterRequestDTO =
            UserRequestDTO.RegisterRequestDTO(
                name = modified.name,
                email = modified.email,
                password = if(modifiedPwd != null) {
                                modifiedPwd
                            } else{
                                password
                            },
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

