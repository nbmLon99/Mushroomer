package com.nbmlon.mushroomer.data.posts

import com.nbmlon.mushroomer.AppUser
import com.nbmlon.mushroomer.api.ResponseCodeConstants.NETWORK_ERROR_CODE
import com.nbmlon.mushroomer.api.dto.BoardRequestDTO
import com.nbmlon.mushroomer.api.service.BoardService
import com.nbmlon.mushroomer.api.service.BoardServiceModule
import com.nbmlon.mushroomer.domain.CommuWriteUseCaseRequest
import com.nbmlon.mushroomer.domain.CommuWriteUseCaseResponse
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
    val service : BoardService = BoardServiceModule().getBoardService()

    override suspend fun uploadPost(domain: CommuWriteUseCaseRequest.UploadPostDomain): CommuWriteUseCaseResponse {
        return try{
            withContext(Dispatchers.IO){
                val dto = BoardRequestDTO.PostRequestDTO(title = domain.post.title, content = domain.post.content, type = domain.post.boardType.serverName)
                val result = service.writePost(AppUser?.token!! , dto = dto, images = domain.uploadImages).await()
                CommuWriteUseCaseResponse.SuccessResponseDomain(true, result.code)
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
                val dto = BoardRequestDTO.PostRequestDTO(title = domain.modified.title, content = domain.modified.content, type = domain.modified.boardType.serverName)
                val result = service.modifyPost(domain.id , dto = dto, images = domain.uploadImages).await()
                CommuWriteUseCaseResponse.SuccessResponseDomain(true, result.code)
            }
        }catch (e : IOException){
            CommuWriteUseCaseResponse.SuccessResponseDomain(false, NETWORK_ERROR_CODE)
        }catch (e : Exception){
            CommuWriteUseCaseResponse.SuccessResponseDomain(false)
        }
    }

}