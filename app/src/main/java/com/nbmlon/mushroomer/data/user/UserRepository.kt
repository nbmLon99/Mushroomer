package com.nbmlon.mushroomer.data.user

import com.nbmlon.mushroomer.api.dto.FindIdRequest
import com.nbmlon.mushroomer.api.dto.FindPwRequest
import com.nbmlon.mushroomer.api.dto.LoginRequest
import com.nbmlon.mushroomer.api.dto.LoginResponse
import com.nbmlon.mushroomer.api.dto.RegisterRequest
import com.nbmlon.mushroomer.api.dto.SuccessResponse
import com.nbmlon.mushroomer.api.dto.UserResponse
import com.nbmlon.mushroomer.api.service.UserService
import retrofit2.Call
import javax.inject.Inject


fun UserRepository() : UserRepository = UserRepositoryImpl()

interface UserRepository {
    suspend fun login(id : String, pw : String) : Call<LoginResponse>
    suspend fun register(registerRequest: RegisterRequest) : Call<SuccessResponse>
    suspend fun findID(findIdRequest: FindIdRequest) : Call<UserResponse>
    suspend fun findPW(findPwRequest: FindPwRequest) : Call<UserResponse>
    suspend fun loginWithToken(refreshToken: String): Call<LoginResponse>
}

private class UserRepositoryImpl: UserRepository {
    @Inject
    private lateinit var backend : UserService
    override suspend fun login(id: String, pw: String): Call<LoginResponse> {
        return backend.login(LoginRequest(id,pw))
    }

    override suspend fun register(registerRequest: RegisterRequest): Call<SuccessResponse> {
        return backend.signUp(registerRequest)
    }

    override suspend fun findID(findIdRequest: FindIdRequest): Call<UserResponse> {
        TODO("Not yet implemented")

    }

    override suspend fun findPW(findPwRequest: FindPwRequest): Call<UserResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun loginWithToken(refreshToken: String): Call<LoginResponse> {
        TODO("Not yet implemented")
    }
    // 기타 서버와의 통신에 필요한 메소드들을 구현




}


