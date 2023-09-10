package com.nbmlon.mushroomer.data.user

import com.nbmlon.mushroomer.api.dto.UserRequestDTO
import com.nbmlon.mushroomer.api.dto.UserRequestDTO.LoginRequestDTO
import com.nbmlon.mushroomer.api.dto.UserRequestDTO.RegisterRequestDTO
import com.nbmlon.mushroomer.api.dto.UserResponseDTO.FindResponseDTO
import com.nbmlon.mushroomer.api.dto.UserResponseDTO.LoginResponseDTO
import com.nbmlon.mushroomer.api.dto.UserResponseDTO.SuccessResponse
import com.nbmlon.mushroomer.api.service.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await
import javax.inject.Inject


fun UserRepository() : UserRepository = UserRepositoryImpl()

interface UserRepository {
    suspend fun login(dto : UserRequestDTO) : LoginResponseDTO
    suspend fun register(dto : UserRequestDTO) : SuccessResponse
    suspend fun findID(dto : UserRequestDTO) : FindResponseDTO
    suspend fun findPW(dto : UserRequestDTO) : FindResponseDTO
    suspend fun loginWithToken(dto : UserRequestDTO): LoginResponseDTO
}

private class UserRepositoryImpl: UserRepository {
    @Inject
    private lateinit var backend : UserService
    override suspend fun login(dto: UserRequestDTO): LoginResponseDTO {
        return withContext(Dispatchers.IO){
            if(dto is LoginRequestDTO){
                val response = backend.login(dto).await()
                if(response is LoginResponseDTO)
                    response
                else
                    LoginResponseDTO(success = false)
            } else{
                LoginResponseDTO(success = false)
            }
        }
    }

    override suspend fun loginWithToken(dto: UserRequestDTO): LoginResponseDTO {
        TODO("Not yet implemented")
    }

    override suspend fun register(dto: UserRequestDTO): SuccessResponse {
        return withContext(Dispatchers.IO) {
            if(dto is RegisterRequestDTO){
                val response =backend.signUp(dto).await()
                if(response is SuccessResponse)
                    response
                else
                    SuccessResponse(false)
            }else {
                SuccessResponse(false)
            }
        }
    }

    override suspend fun findID(dto: UserRequestDTO): FindResponseDTO {
        return withContext(Dispatchers.IO){
            TODO("백엔드 아이디찾기")
        }
    }

    override suspend fun findPW(dto: UserRequestDTO): FindResponseDTO {
        TODO("Not yet implemented")
    }


    // 기타 서버와의 통신에 필요한 메소드들을 구현




}


