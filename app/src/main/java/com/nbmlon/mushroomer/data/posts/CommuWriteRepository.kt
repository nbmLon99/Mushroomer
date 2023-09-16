package com.nbmlon.mushroomer.data.posts

import com.nbmlon.mushroomer.AppUser
import com.nbmlon.mushroomer.api.ResponseCodeConstants.NETWORK_ERROR_CODE
import com.nbmlon.mushroomer.api.service.BoardService
import com.nbmlon.mushroomer.domain.CommuWriteUseCaseRequest
import com.nbmlon.mushroomer.domain.CommuWriteUseCaseResponse
import com.nbmlon.mushroomer.domain.toWriteDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await
import java.io.IOException
import javax.inject.Inject

interface CommuWriteRepository {
    suspend fun uploadPost(domain : CommuWriteUseCaseRequest.UploadPostDomain) : CommuWriteUseCaseResponse
    suspend fun modifyPost(domain : CommuWriteUseCaseRequest.ModifiyPostDomain) : CommuWriteUseCaseResponse

}

fun CommuWriteRepository() : CommuWriteRepository = CommuWriteRepositoryImpl()


class CommuWriteRepositoryImpl : CommuWriteRepository {
    @Inject
    lateinit var service : BoardService

    override suspend fun uploadPost(domain: CommuWriteUseCaseRequest.UploadPostDomain): CommuWriteUseCaseResponse {
        return try{
            withContext(Dispatchers.IO){
                service.writePost(AppUser?.user!!.id, post = domain.post).await().toWriteDomain()
            }
        }catch (e : IOException){
            CommuWriteUseCaseResponse.SuccessResponseDomain(false, NETWORK_ERROR_CODE)
        }catch (e : Exception){
            CommuWriteUseCaseResponse.SuccessResponseDomain(false)
        }
    }

    override suspend fun modifyPost(domain: CommuWriteUseCaseRequest.ModifiyPostDomain): CommuWriteUseCaseResponse {
        return try{
            withContext(Dispatchers.IO){
                service.modifyPost(AppUser?.user!!.id, domain.id, domain.modified).await().toWriteDomain()
            }
        }catch (e : IOException){
            CommuWriteUseCaseResponse.SuccessResponseDomain(false, NETWORK_ERROR_CODE)
        }catch (e : Exception){
            CommuWriteUseCaseResponse.SuccessResponseDomain(false)
        }
    }

}