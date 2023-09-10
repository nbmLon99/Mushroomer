package com.nbmlon.mushroomer.api.service

import com.nbmlon.mushroomer.api.dto.LoginRequest
import com.nbmlon.mushroomer.api.dto.LoginResponse
import com.nbmlon.mushroomer.api.dto.RegisterRequest
import com.nbmlon.mushroomer.api.dto.SuccessResponse
import com.nbmlon.mushroomer.api.dto.UserResponse
import dagger.Module
import dagger.Provides
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserService {
    //로그인
    @POST("/login")
    suspend fun login(@Body request: LoginRequest) : Call<LoginResponse>

    //토큰 재발급
    @POST("/users/token/generateToken")
    suspend fun generateToken() : Call<UserResponse>

    //소셜 로그인
    @POST("/api/users/oauth2/login")
    suspend fun kakaoLogin() : Call<UserResponse>

    //유저 정보 변경
    @PUT("/users/{userId}")
    suspend fun modifyUser(@Path("userId")id:Int, @Body request : RegisterRequest) : Call<UserResponse>

    //회원 탈퇴
    @DELETE("/users/{userId}")
    suspend fun withdrawal(@Path("userId")id : Int) : Call<UserResponse>

    //회원가입
    @POST("/users/join")
    suspend fun signUp(@Body request : RegisterRequest) : Call<SuccessResponse>


}

@Module
class UserServiceModule {
    @Provides
    fun provideUserService(retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }
}



