package com.nbmlon.mushroomer.data.user

import com.nbmlon.mushroomer.api.ResponseCodeConstants
import com.nbmlon.mushroomer.api.service.UserService
import com.nbmlon.mushroomer.domain.CommuPostUseCaseResponse
import com.nbmlon.mushroomer.domain.LoginUseCaseRequest
import com.nbmlon.mushroomer.domain.LoginUseCaseRequest.LoginRequestDomain
import com.nbmlon.mushroomer.domain.LoginUseCaseRequest.RegisterRequestDomain
import com.nbmlon.mushroomer.domain.LoginUseCaseRequest.TokenLoginRequestDomain
import com.nbmlon.mushroomer.domain.LoginUseCaseResponse
import com.nbmlon.mushroomer.domain.LoginUseCaseResponse.FindResponseDomain
import com.nbmlon.mushroomer.domain.toDTO
import com.nbmlon.mushroomer.domain.toLoginDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await
import java.io.IOException
import javax.inject.Inject


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
    suspend fun findID(domain : LoginUseCaseRequest) : LoginUseCaseResponse
    suspend fun findPW(domain : LoginUseCaseRequest) : LoginUseCaseResponse
    suspend fun loginWithToken(domain : TokenLoginRequestDomain): LoginUseCaseResponse
}

private class UserRepositoryImpl: UserRepository {
    @Inject
    private lateinit var service : UserService
    override suspend fun login(domain: LoginRequestDomain): LoginUseCaseResponse {
        return try{
            withContext(Dispatchers.IO){
                service.login(domain.toDTO()).await().toLoginDomain()
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

    override suspend fun loginWithToken(domain: TokenLoginRequestDomain): LoginUseCaseResponse {
        TODO(" 토큰로그인 ")
    }

    override suspend fun register(domain : RegisterRequestDomain): LoginUseCaseResponse {
        return try{
            withContext(Dispatchers.IO) {
                service.signUp(domain.toDTO()).await().toLoginDomain()
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

    override suspend fun findID(domain: LoginUseCaseRequest): LoginUseCaseResponse {
        return try {
            withContext(Dispatchers.IO){
                TODO("백엔드 아이디찾기")
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

    override suspend fun findPW(domain: LoginUseCaseRequest): LoginUseCaseResponse {
        return try{
            withContext(Dispatchers.IO){
                var responseDTO = FindResponseDomain(success = false)
                TODO("백엔드 비밀번호 찾기찾기")
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


