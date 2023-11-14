package com.nbmlon.mushroomer.api.service

import com.nbmlon.mushroomer.api.dto.DefaultResponseDTO
import com.nbmlon.mushroomer.api.dto.UserRequestDTO.*
import com.nbmlon.mushroomer.api.dto.UserResponseDTO.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface UserService {
    //특정 회원 조회
    @GET("/api/users/{userId}")
    suspend fun findUser(@Path("userId") id : Int) //

    //소셜 로그인
    @POST("/api/users/oauth2/login")
    suspend fun kakaoLogin(

    ) : Call<LoginResponseDTO>

    //자체 로그인
    @POST("/api/users/login")
    suspend fun login(
        @Body dto: LoginRequestDTO
    ) : Call<LoginResponseDTO>

    //로그아웃
    @POST("/api/users/logout")
    suspend fun logout(
        @Body dto : LoginRequestDTO
    ) : Call<DefaultResponseDTO> //

    //자체 회원가입
    @POST("/api/users/signup")
    suspend fun signUp(
        @Body dto : RegisterRequestDTO
    ) : Call<DefaultResponseDTO>

    //프로필 사진 변경
    @Multipart
    @PATCH("/api/users/edit/profile-image")
    suspend fun changeIcon(
        @Header("Authorization")token : String,
        @Part images: MultipartBody.Part
    ): Call<DefaultResponseDTO>


    //닉네임 변경
    @PATCH("/api/users/edit/nickname")
    suspend fun changeNickname(
        @Header("Authorization")token : String,
        @Body dto : EditNicknameDTO
    ): Call<DefaultResponseDTO>


    //회원 탈퇴
    @DELETE("/api/users/withdraw")
    suspend fun withdrawal(
        @Header("Authorization")token : String,
    ) : Call<DefaultResponseDTO>


    //TODO
    suspend fun changePassword(
        @Header("Authorization") token: String
    ): Call<DefaultResponseDTO>


    //SMS 인증
    @POST("/api/login/sendSMS")
    suspend fun sendSMS(
        @Body target : TargetPhoneDTO
    ) : Call<AuthCodeResponseDTO>

    //Email인증
    @POST("/api/login/sendEmail")
    suspend fun sendEmail(
        @Body  target : TargetEmailDTO
    ) : Call<AuthCodeResponseDTO>

    //아이디 찾기
    @POST("/api/login/findId")
    suspend fun findId(
        @Body target : TargetPhoneDTO
    ) : Call<FoundIdDTO>

    //비밀번호 찾기
    @POST("/api/login/findPassword")
    suspend fun findPassword(
        @Body target : TargetEmailDTO
    ) : Call<FoundPasswordDTO>

    /**
    //토큰 재발급
    @POST("/users/token/generateToken")
    suspend fun generateToken() : Call<GenerateTokenResponseDTO>
    //유저 정보 변경
    @PUT("/users/{userId}")
    suspend fun modifyUser(@Path("userId")id:Int, @Body request : RegisterRequestDTO) : Call<ModifyUserResponseDTO>
    **/
}

@Module
@InstallIn(ViewModelComponent::class)
class UserServiceModule {
    @Provides
    fun provideUserService(retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }
}



