package com.nbmlon.mushroomer.data.user

import com.nbmlon.mushroomer.AppUser
import com.nbmlon.mushroomer.api.ResponseCodeConstants
import com.nbmlon.mushroomer.api.dto.UserRequestDTO
import com.nbmlon.mushroomer.api.service.UserService
import com.nbmlon.mushroomer.api.service.UserServiceModule
import com.nbmlon.mushroomer.domain.ProfileUseCaseRequest
import com.nbmlon.mushroomer.domain.ProfileUseCaseResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await
import java.io.IOException
import javax.inject.Inject

fun ProfileRepository() : ProfileRepository = ProfileRepositoryImpl()


interface  ProfileRepository{
    suspend fun withdrawal(domain : ProfileUseCaseRequest.WithdrawalRequestDomain) : ProfileUseCaseResponse
    suspend fun modifyPwd(domain: ProfileUseCaseRequest.ModifyPwdRequestDomain) : ProfileUseCaseResponse
    suspend fun modifyNickname(domain: ProfileUseCaseRequest.ModifyNicknameRequestDomain) : ProfileUseCaseResponse
    suspend fun modifyIcon(domain: ProfileUseCaseRequest.ModifyIconRequestDomain) : ProfileUseCaseResponse

}


private class ProfileRepositoryImpl : ProfileRepository {
    val service : UserService = UserServiceModule().getUserService()

    override suspend fun withdrawal(domain: ProfileUseCaseRequest.WithdrawalRequestDomain): ProfileUseCaseResponse {
        return try{
            withContext(Dispatchers.IO){
                val result = service.withdrawal(AppUser.token!!).await()
                ProfileUseCaseResponse.SuccessResponseDomain(
                    success = true,
                    code = result.code
                )
            }
        }catch (e : IOException){
            ProfileUseCaseResponse.SuccessResponseDomain(
                false,
                ResponseCodeConstants.NETWORK_ERROR_CODE
            )
        }catch (e : Exception){
            ProfileUseCaseResponse.SuccessResponseDomain(
                false,
                ResponseCodeConstants.UNDEFINED_ERROR_CODE
            )
        }
    }

    override suspend fun modifyPwd(domain: ProfileUseCaseRequest.ModifyPwdRequestDomain): ProfileUseCaseResponse {
        return try{
            withContext(Dispatchers.IO){
                val result = service.changePassword(AppUser.token!!).await()
                ProfileUseCaseResponse.SuccessResponseDomain(
                    success = true,
                    code = result.code
                )
            }
        }catch (e : IOException){
            ProfileUseCaseResponse.SuccessResponseDomain(
                false,
                ResponseCodeConstants.NETWORK_ERROR_CODE
            )
        }catch (e : Exception){
            ProfileUseCaseResponse.SuccessResponseDomain(
                false,
                ResponseCodeConstants.UNDEFINED_ERROR_CODE
            )
        }
    }

    override suspend fun modifyNickname(domain: ProfileUseCaseRequest.ModifyNicknameRequestDomain): ProfileUseCaseResponse {
        return try{
            withContext(Dispatchers.IO){
                val result = service.changeNickname(AppUser.token!!, UserRequestDTO.EditNicknameDTO(domain.nickname)).await()
                ProfileUseCaseResponse.SuccessResponseDomain(
                    success = true,
                    result.code
                )
            }
        }catch (e : IOException){
            ProfileUseCaseResponse.SuccessResponseDomain(
                false,
                ResponseCodeConstants.NETWORK_ERROR_CODE
            )
        }catch (e : Exception){
            ProfileUseCaseResponse.SuccessResponseDomain(
                false,
                ResponseCodeConstants.UNDEFINED_ERROR_CODE
            )
        }
    }

    override suspend fun modifyIcon(domain: ProfileUseCaseRequest.ModifyIconRequestDomain): ProfileUseCaseResponse {
        return try{
            withContext(Dispatchers.IO){
                val result = service.changeIcon(AppUser.token!!, domain.icon).await()
                ProfileUseCaseResponse.SuccessResponseDomain(
                    success = true,
                    result.code
                )
            }
        }catch (e : IOException){
            ProfileUseCaseResponse.SuccessResponseDomain(
                false,
                ResponseCodeConstants.NETWORK_ERROR_CODE
            )
        }catch (e : Exception){
            ProfileUseCaseResponse.SuccessResponseDomain(
                false,
                ResponseCodeConstants.UNDEFINED_ERROR_CODE
            )
        }
    }

}