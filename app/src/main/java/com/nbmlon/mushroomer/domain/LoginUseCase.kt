package com.nbmlon.mushroomer.domain

import com.nbmlon.mushroomer.MyApplication
import com.nbmlon.mushroomer.api.EndConverter.sha256
import com.nbmlon.mushroomer.api.ResponseCodeConstants.UNDEFINED_ERROR_CODE
import com.nbmlon.mushroomer.api.dto.UserRequestDTO
import com.nbmlon.mushroomer.api.dto.UserResponseDTO.GenerateTokenResponseDTO
import com.nbmlon.mushroomer.api.dto.UserResponseDTO.KakaoLoginResponseDTO
import com.nbmlon.mushroomer.api.dto.UserResponseDTO.LoginResponseDTO
import com.nbmlon.mushroomer.api.dto.UserResponseDTO.ModifyUserResponseDTO
import com.nbmlon.mushroomer.api.dto.UserResponseDTO.SignUpResponseDTO
import com.nbmlon.mushroomer.api.dto.UserResponseDTO.WithdrawalResponseDTO
import com.nbmlon.mushroomer.domain.LoginUseCaseRequest.LoginRequestDomain
import com.nbmlon.mushroomer.domain.LoginUseCaseRequest.RegisterRequestDomain
import com.nbmlon.mushroomer.domain.LoginUseCaseResponse.GenerateTokenResponseDomain
import com.nbmlon.mushroomer.domain.LoginUseCaseResponse.LoginResponseDomain
import com.nbmlon.mushroomer.domain.LoginUseCaseResponse.SuccessResponseDomain
import com.nbmlon.mushroomer.model.User
import java.security.MessageDigest


/** request**/
sealed class LoginUseCaseRequest{
    data class LoginRequestDomain(
        val email: String,
        val password: String
    ): LoginUseCaseRequest()

    data class TokenLoginRequestDomain(
        val token : String
    ) : LoginUseCaseRequest()

    data class RegisterRequestDomain(
        val name : String,
        val email : String,
        val password : String,
        val nickname : String,
        val cellphone : String
    ): LoginUseCaseRequest()

    data class EditRequestDomain(
        val name : String,
        val password : String,
        val nickname : String,
        val cellphone : String,
        val imageUrl : String
    ): LoginUseCaseRequest()

    data class FindIdRequestDomain(
        val name : String,
        val cellphone: String
    ): LoginUseCaseRequest()

    data class FindPwRequestDomain(
        val name : String,
        val email : String
    ) : LoginUseCaseRequest()
}



fun LoginRequestDomain.toDTO() : UserRequestDTO.LoginRequestDTO =
    UserRequestDTO.LoginRequestDTO(
        email = email,
        password = sha256(password)
    )

fun LoginUseCaseRequest.EditRequestDomain.toDTO() =
    UserRequestDTO.EditRequestDTO(
        name = name,
        password = sha256(password),
        nickname = nickname,
        cellphone = cellphone,
        imageUrl = imageUrl
    )


fun RegisterRequestDomain.toDTO() : UserRequestDTO.RegisterRequestDTO =
    UserRequestDTO.RegisterRequestDTO(
        name = name,
        email = email,
        password = sha256(password),
        nickname = nickname,
        cellphone = cellphone
    )



/** response **/
sealed class LoginUseCaseResponse{
    abstract val success : Boolean
    abstract val code : Int
    data class LoginResponseDomain(
        override val success: Boolean = false,
        override val code : Int = UNDEFINED_ERROR_CODE,
        val refreshToken : String? = null,
        val token : String? = null,
        val loginUser : User? = null,
        val percentage : Int? = null
    ) : LoginUseCaseResponse()

    data class FindResponseDomain(
        override val success: Boolean = false,
        override val code : Int = -1,
        val hint : String = ""
    ) : LoginUseCaseResponse()

    data class SuccessResponseDomain(
        override val success: Boolean = false,
        override val code : Int = UNDEFINED_ERROR_CODE,
        val message: String = ""
    ) : LoginUseCaseResponse()

    data class GenerateTokenResponseDomain(
        override val success: Boolean = false,
        override val code : Int = UNDEFINED_ERROR_CODE,
        val token : String
    ) : LoginUseCaseResponse()
}


fun LoginResponseDTO.toLoginDomain() : LoginUseCaseResponse =
    LoginResponseDomain(
        success = success,
        refreshToken = refreshToken,
        token = token,
        loginUser = loginUser,
        percentage = percentage
    )

fun KakaoLoginResponseDTO.toLoginDomain() : LoginUseCaseResponse =
    LoginResponseDomain(
        success = success,
        refreshToken = refreshToken,
        token = token,
        loginUser = loginUser,
        percentage = percentage
    )

fun GenerateTokenResponseDTO.toLoginDomain() : LoginUseCaseResponse =
    GenerateTokenResponseDomain(
        token = message
    )

fun ModifyUserResponseDTO.toLoginDomain() : LoginUseCaseResponse =
    SuccessResponseDomain(success = success)
fun WithdrawalResponseDTO.toLoginDomain(): LoginUseCaseResponse =
    SuccessResponseDomain(success = success)
fun SignUpResponseDTO.toLoginDomain(): LoginUseCaseResponse =
    SuccessResponseDomain(success = success)


