package com.nbmlon.mushroomer.domain

import android.graphics.Bitmap
import android.net.Uri
import com.nbmlon.mushroomer.api.EndConverter.sha256
import com.nbmlon.mushroomer.api.ResponseCodeConstants
import com.nbmlon.mushroomer.api.dto.UserRequestDTO
import com.nbmlon.mushroomer.api.dto.UserResponseDTO
import com.nbmlon.mushroomer.model.User
import okhttp3.MultipartBody

sealed class ProfileUseCaseRequest{
    data class WithdrawalRequestDomain(val email : String, val password : String) : ProfileUseCaseRequest(){
        fun toDTO() : UserRequestDTO.WithdrawalRequestDTO =
            UserRequestDTO.WithdrawalRequestDTO(
                email = email, password = sha256(password)
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
                                sha256(modifiedPwd)
                            } else{
                                sha256(password)
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


fun UserResponseDTO.WithdrawalResponseDTO.toProfileDomain() : ProfileUseCaseResponse =
    ProfileUseCaseResponse.SuccessResponseDomain(
        success = success,
        code = code
    )