package com.nbmlon.mushroomer.data.user

import android.util.Log
import com.nbmlon.mushroomer.api.ResponseCodeConstants
import com.nbmlon.mushroomer.api.dto.UserRequestDTO
import com.nbmlon.mushroomer.api.service.UserService
import com.nbmlon.mushroomer.api.service.UserServiceModule
import com.nbmlon.mushroomer.domain.LoginUseCaseRequest
import com.nbmlon.mushroomer.domain.LoginUseCaseRequest.LoginRequestDomain
import com.nbmlon.mushroomer.domain.LoginUseCaseRequest.RegisterRequestDomain
import com.nbmlon.mushroomer.domain.LoginUseCaseRequest.TokenLoginRequestDomain
import com.nbmlon.mushroomer.domain.LoginUseCaseResponse
import com.nbmlon.mushroomer.domain.LoginUseCaseResponse.FindResponseDomain
import com.nbmlon.mushroomer.domain.toDTO
import com.nbmlon.mushroomer.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


/**
 *  Repository 단에서
 *  UserRequestDTO의 형식을 검증하여 service단으로 넘김
 *
 *
 *  Fragment
 *  ViewModel
 *                      LoginUseCaseDomain
 *  Repository (변환 )
 *                      UserServiceDTO
 *  Service
 *  **/

fun UserRepository() : UserRepository = UserRepositoryImpl()

interface UserRepository {
    suspend fun login(domain : LoginRequestDomain) : LoginUseCaseResponse
    suspend fun register(domain : RegisterRequestDomain) : LoginUseCaseResponse
    suspend fun findID(domain : LoginUseCaseRequest.FindIdRequestDomain) : LoginUseCaseResponse
    suspend fun findPW(domain : LoginUseCaseRequest.FindPwRequestDomain) : LoginUseCaseResponse
    suspend fun loginWithToken(domain : TokenLoginRequestDomain): LoginUseCaseResponse
}

private class UserRepositoryImpl : UserRepository{
    val service = UserServiceModule().getUserService()
    override suspend fun login(domain: LoginRequestDomain): LoginUseCaseResponse {
        return try{
            withContext(Dispatchers.IO){
                val result = service.login(domain.toDTO()).await()
                return@withContext LoginUseCaseResponse.LoginResponseDomain(
                    success = true,
                    code = ResponseCodeConstants.SUCCESS_CODE,
                    refreshToken = result.refreshToken,
                    token = result.accessToken,
                    loginUser = User(
                        id = result.id, email = result.email, name = result.name, nickname = result.nickname, icon = result.imageUrl, phone_number = result.phone
                    ),
                    percentage = result.percentage
                )
            }
        }catch (e : IOException){
            LoginUseCaseResponse.SuccessResponseDomain(false,
                ResponseCodeConstants.NETWORK_ERROR_CODE
            )
        }catch (e : Exception){
            e.printStackTrace()
            Log.e("api",e.toString())
            LoginUseCaseResponse.SuccessResponseDomain(false,
                ResponseCodeConstants.UNDEFINED_ERROR_CODE
            )
        }
    }

    override suspend fun loginWithToken(domain: TokenLoginRequestDomain): LoginUseCaseResponse {
        TODO(" 토큰로그인 ")
    }

    override suspend fun register(domain : RegisterRequestDomain): LoginUseCaseResponse {
        return try{
            Log.d("api","회원가입 요청 완료")
            withContext(Dispatchers.IO) {
                val result = service.signUp(domain.toDTO()).await()
                LoginUseCaseResponse.SuccessResponseDomain(
                    success = result.code != -1,
                    code = result.code,
                    message = result.message
                )
            }
        }catch (e : IOException){
            LoginUseCaseResponse.SuccessResponseDomain(false,
                ResponseCodeConstants.NETWORK_ERROR_CODE
            )
        }catch (e : Exception){
            e.printStackTrace()
            Log.e("api",e.toString())
            LoginUseCaseResponse.SuccessResponseDomain(false,
                ResponseCodeConstants.UNDEFINED_ERROR_CODE
            )
        }
    }

    override suspend fun findID(domain: LoginUseCaseRequest.FindIdRequestDomain): LoginUseCaseResponse {
        return try {
            withContext(Dispatchers.IO){
                val result = service.findId(UserRequestDTO.TargetPhoneDTO(domain.cellphone)).await()
                FindResponseDomain(
                    success = true,
                    code = ResponseCodeConstants.SUCCESS_CODE,
                    hint = result.userEmail
                )
            }
        }catch (e : IOException){
            LoginUseCaseResponse.SuccessResponseDomain(false,
                ResponseCodeConstants.NETWORK_ERROR_CODE
            )
        }catch (e : Exception){
            LoginUseCaseResponse.SuccessResponseDomain(false,
                ResponseCodeConstants.UNDEFINED_ERROR_CODE
            )
        }
    }

    override suspend fun findPW(domain: LoginUseCaseRequest.FindPwRequestDomain): LoginUseCaseResponse {
        return try{
            withContext(Dispatchers.IO){
                val result = service.findPassword(UserRequestDTO.TargetEmailDTO(domain.email)).await()
                FindResponseDomain(
                    success = true,
                    code = ResponseCodeConstants.SUCCESS_CODE,
                    hint = result.password
                )
            }
        }catch (e : IOException){
            LoginUseCaseResponse.SuccessResponseDomain(false,
                ResponseCodeConstants.NETWORK_ERROR_CODE
            )
        }catch (e : Exception){
            LoginUseCaseResponse.SuccessResponseDomain(false,
                ResponseCodeConstants.UNDEFINED_ERROR_CODE
            )
        }
    }
    // 기타 서버와의 통신에 필요한 메소드들을 구현
}


