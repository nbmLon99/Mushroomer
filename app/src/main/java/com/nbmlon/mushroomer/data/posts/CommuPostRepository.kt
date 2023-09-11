package com.nbmlon.mushroomer.data.posts

import com.nbmlon.mushroomer.api.dto.CommuPostRequestDTO
import com.nbmlon.mushroomer.api.dto.CommuPostResponseDTO
import com.nbmlon.mushroomer.api.service.BoardService
import com.nbmlon.mushroomer.api.service.CommentService
import com.nbmlon.mushroomer.api.service.ReportService
import com.nbmlon.mushroomer.model.Post
import com.nbmlon.mushroomer.ui.commu.post.CommuPostResponse
import com.nbmlon.mushroomer.ui.commu.post.TargetType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await
import javax.inject.Inject


/** 특정 게시판 열린 상태에서 사용할 repo **/
interface CommuPostRepository {
    suspend fun report(targetType : TargetType, dto : CommuPostRequestDTO) : CommuPostResponse
    suspend fun delete(targetType : TargetType, dto : CommuPostRequestDTO) : CommuPostResponse
    suspend fun uploadComment(dto : CommuPostRequestDTO) : CommuPostResponse
    suspend fun modifyComment(dto : CommuPostRequestDTO) : CommuPostResponse
    suspend fun loadPost(dto: CommuPostRequestDTO) : Post
}

fun CommuPostRepository() : CommuPostRepository = CommuPostRepositoryImpl()




class CommuPostRepositoryImpl : CommuPostRepository {
    @Inject private lateinit var boardService : BoardService
    @Inject private lateinit var reportService : ReportService
    @Inject private lateinit var commentService : CommentService

    override suspend fun report(targetType: TargetType, dto: CommuPostRequestDTO): CommuPostResponse {
        var responseDTO = CommuPostResponseDTO.SuccessResponseDTO(false)

        when (targetType){
            TargetType.POST->{
                if(dto is CommuPostRequestDTO.ReportDTO){
                    responseDTO = reportService.report(dto.id).await()
                }
            }
            TargetType.COMMENT->{
                if(dto is CommuPostRequestDTO.ReportDTO){
                    val response = reportService.report(dto.id).await()
                    if( response is CommuPostResponseDTO.SuccessResponseDTO )
                        responseDTO = response
                }
            }
        }
        CommuPostResponse.SuccessResponse(responseDTO)
    }

    override suspend fun delete(targetType: TargetType, dto: CommuPostRequestDTO): CommuPostResponse {
        when (targetType){
            TargetType.POST->{
                boardService.deleteBoard(dto)
            }
            TargetType.COMMENT->{
                commentService.deleteComment(dto)
            }
        }
    }

    override suspend fun uploadComment(dto: CommuPostRequestDTO): CommuPostResponse {
        TODO("Not yet implemented")
    }

    override suspend fun modifyComment(dto: CommuPostRequestDTO): CommuPostResponse {
        TODO("Not yet implemented")
    }

    override suspend fun loadPost(dto: CommuPostRequestDTO): CommuPostResponse {
        var response : CommuPostResponse = CommuPostResponse.SuccessResponse(CommuPostResponseDTO.SuccessResponseDTO(false))
        if(dto is CommuPostRequestDTO.LoadPostDTO)
        withContext(Dispatchers.IO){
            response = CommuPostResponse.LoadedPostResponse(boardService.getPost(dto.id).await())
        }
        response
    }


}
