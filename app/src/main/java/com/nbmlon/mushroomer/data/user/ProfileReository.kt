package com.nbmlon.mushroomer.data.user

import com.nbmlon.mushroomer.AppUser
import com.nbmlon.mushroomer.api.ResponseCodeConstants
import com.nbmlon.mushroomer.api.service.UserService
import com.nbmlon.mushroomer.domain.ProfileUseCaseRequest
import com.nbmlon.mushroomer.domain.ProfileUseCaseResponse
import com.nbmlon.mushroomer.domain.toProfileDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await
import java.io.IOException
import javax.inject.Inject

fun ProfileRepository() : ProfileRepository = ProfileRepositoryImpl()


interface  ProfileRepository{
    suspend fun withdrawal(domain : ProfileUseCaseRequest.WithdrawalRequestDomain) : ProfileUseCaseResponse
    suspend fun modifyProfile(domain: ProfileUseCaseRequest.ModifyProfileRequestDomain) : ProfileUseCaseResponse
}


private class ProfileRepositoryImpl : ProfileRepository {
    @Inject
    lateinit var service : UserService

    override suspend fun withdrawal(domain: ProfileUseCaseRequest.WithdrawalRequestDomain): ProfileUseCaseResponse {
        return try{
            withContext(Dispatchers.IO){
                service.withdrawal(AppUser.user!!.id).await().toProfileDomain()
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

    override suspend fun modifyProfile(domain: ProfileUseCaseRequest.ModifyProfileRequestDomain): ProfileUseCaseResponse {
        return try{
            withContext(Dispatchers.IO){
                service.withdrawal(AppUser.user!!.id).await().toProfileDomain()
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