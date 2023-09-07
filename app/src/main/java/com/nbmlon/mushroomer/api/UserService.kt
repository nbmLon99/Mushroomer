package com.nbmlon.mushroomer.api

import com.google.gson.annotations.SerializedName
import com.nbmlon.mushroomer.model.User
import dagger.Module
import dagger.Provides
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserService {
    //로그인
    @POST("/login")
    suspend fun login() : Call<UserRespone>

    //토큰 재발급
    @POST("/users/token/generateToken")
    suspend fun generateToken() : Call<UserRespone>

    //소셜 로그인
    @POST("/api/users/oauth2/login")
    suspend fun kakaoLogin() : Call<UserRespone>

    //유저 정보 변경
    @PUT("/users/{userId}")
    suspend fun modifyUser(@Path("userId")id:Int) : Call<UserRespone>

    //회원 탈퇴
    @DELETE("/users/{userId}")
    suspend fun withdrawal(@Path("userId")id : Int) : Call<UserRespone>

    //회원가입
    @POST("/users/join")
    suspend fun signUp(user : User) : Call<SuccessResponse>


}

@Module
class UserServiceModule {
    @Provides
    fun provideUserService(retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }
}

data class UserRespone(
    @SerializedName("tokens") val token : String,
    @SerializedName("") val items : User,
    @SerializedName("isSuccess") val isSuccess : Boolean
)