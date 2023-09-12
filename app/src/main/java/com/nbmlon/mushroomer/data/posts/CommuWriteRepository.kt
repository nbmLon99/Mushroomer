package com.nbmlon.mushroomer.data.posts

import com.nbmlon.mushroomer.domain.CommuWriteRequestDTO
import com.nbmlon.mushroomer.domain.CommuWriteResponseDTO
import com.nbmlon.mushroomer.api.service.BoardService
import com.nbmlon.mushroomer.ui.commu.board.CommuWriteResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await
import javax.inject.Inject

interface CommuWriteRepository {
    suspend fun uploadPost(dto : CommuWriteRequestDTO) : CommuWriteResponse
    suspend fun modifyPost(dto : CommuWriteRequestDTO) : CommuWriteResponse

}

fun CommuWriteRepository() : CommuWriteRepository = CommuWriteRepositoryImpl()


class CommuWriteRepositoryImpl : CommuWriteRepository {
    @Inject
    private lateinit var service : BoardService



    override suspend fun uploadPost(dto: CommuWriteRequestDTO): CommuWriteResponse {
        val responseDTO  = CommuWriteResponseDTO.SuccessResponse(false)
        withContext(Dispatchers.IO){
            if( dto is CommuWriteRequestDTO.UploadPostDTO ){
                responseDTO =  service.writePost(dto.post.id, dto.post).await()
            }
        }


        CommuWriteResponse
        TODO("Not yet implemented")
    }

    override suspend fun modifyPost(dto: CommuWriteRequestDTO): CommuWriteResponse {
        TODO("Not yet implemented")
    }

}