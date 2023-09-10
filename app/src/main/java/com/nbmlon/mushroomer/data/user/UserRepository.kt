package com.nbmlon.mushroomer.data.user

import com.nbmlon.mushroomer.api.dto.UserRequestDTO
import com.nbmlon.mushroomer.api.dto.UserRequestDTO.LoginRequestDTO
import com.nbmlon.mushroomer.api.dto.UserRequestDTO.RegisterRequestDTO
import com.nbmlon.mushroomer.api.dto.UserRequestDTO.TokenLoginRequestDTO
import com.nbmlon.mushroomer.api.dto.UserResponseDTO.FindResponseDTO
import com.nbmlon.mushroomer.api.dto.UserResponseDTO.LoginResponseDTO
import com.nbmlon.mushroomer.api.dto.UserResponseDTO.SuccessResponse
import com.nbmlon.mushroomer.api.service.UserService
import com.nbmlon.mushroomer.ui.login.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await
import javax.inject.Inject



/**
 *  Repository 단에서
 *  UserRequestDTO의 형식을 검증하여 service단으로 넘김
 *
 *
 *  Fragment
 *                      UserRequest / UserResponse
 *  ViewModel
 *                      UserRequestDTO / UserRequestDTO
 *  Repository
 *                      (LoginRequestDTO , RegisterRequestDTO, ... ) / ( LoginResponseDTO, RegisterRequest, ...)
 *  Service
 *  **/

fun UserRepository() : UserRepository = UserRepositoryImpl()

interface UserRepository {
    suspend fun login(dto : UserRequestDTO) : UserResponse
    suspend fun register(dto : UserRequestDTO) : UserResponse
    suspend fun findID(dto : UserRequestDTO) : UserResponse
    suspend fun findPW(dto : UserRequestDTO) : UserResponse
    suspend fun loginWithToken(dto : UserRequestDTO): UserResponse
}

private class UserRepositoryImpl: UserRepository {
    @Inject
    private lateinit var backend : UserService
    override suspend fun login(dto: UserRequestDTO): UserResponse {
        return withContext(Dispatchers.IO){
            var responseDTO = LoginResponseDTO(success = false)
            if(dto is LoginRequestDTO) {
                val response = backend.login(dto).await()
                if (response is LoginResponseDTO)
                    responseDTO = response
            }

            UserResponse.Login(responseDTO)
        }
    }

    override suspend fun loginWithToken(dto: UserRequestDTO): UserResponse {
        return withContext(Dispatchers.IO){
            var responseDTO = LoginResponseDTO(success = false)
            if(dto is TokenLoginRequestDTO) {
                //val response = backend.tokenLogin(dto).await()
                //if (response is LoginResponseDTO)
                //    responseDTO = response
            }

            UserResponse.Login(responseDTO)
        }
    }

    override suspend fun register(dto: UserRequestDTO): UserResponse {
        return withContext(Dispatchers.IO) {
            var responseDTO = SuccessResponse(success = false)
            if(dto is RegisterRequestDTO) {
                val response = backend.signUp(dto).await()
                if (response is SuccessResponse)
                    response
            }
            UserResponse.Register(responseDTO)
        }
    }

    override suspend fun findID(dto: UserRequestDTO): UserResponse {
        return withContext(Dispatchers.IO){
            var responseDTO = FindResponseDTO(success = false)
            TODO("백엔드 아이디찾기")
        }
    }

    override suspend fun findPW(dto: UserRequestDTO): UserResponse {
        return withContext(Dispatchers.IO){
            var responseDTO = FindResponseDTO(success = false)
            TODO("백엔드 비밀번호 찾기찾기")
            UserResponse.FindIdPw(responseDTO)
        }
    }


    // 기타 서버와의 통신에 필요한 메소드들을 구현




}


