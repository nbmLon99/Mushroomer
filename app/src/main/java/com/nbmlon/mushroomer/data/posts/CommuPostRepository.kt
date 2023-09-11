package com.nbmlon.mushroomer.data.posts

import com.nbmlon.mushroomer.api.dto.CommuPostRequestDTO
import com.nbmlon.mushroomer.api.dto.CommuPostResponseDTO
import com.nbmlon.mushroomer.api.service.BoardService
import com.nbmlon.mushroomer.api.service.CommentService
import com.nbmlon.mushroomer.api.service.ReportService
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
    suspend fun upload(targetType : TargetType, dto : CommuPostRequestDTO) : CommuPostResponse
    suspend fun modify(targetType : TargetType, dto : CommuPostRequestDTO) : CommuPostResponse
}

fun CommuPostRepository() : CommuPostRepository = CommuPostRepositoryImpl()




class CommuPostRepositoryImpl : CommuPostRepository {
    @Inject private lateinit var boardService : BoardService
    @Inject private lateinit var reportService : ReportService
    @Inject private lateinit var commentService : CommentService

    override suspend fun report(targetType: TargetType, dto: CommuPostRequestDTO): CommuPostResponse {
        when (targetType){
            TargetType.POST->{
                reportService.report(dto)
            }
            TargetType.COMMENT->{
                reportService.report(dto)
            }
        }
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

    override suspend fun upload(targetType: TargetType, dto: CommuPostRequestDTO): CommuPostResponse {
        when (targetType){
            TargetType.POST->{
                boardService.writePost(dto)
            }
            TargetType.COMMENT->{
                commentService.writeComment(dto)
            }
        }
    }

    override suspend fun modify(targetType: TargetType, dto: CommuPostRequestDTO): CommuPostResponse {
        withContext(Dispatchers.IO){
            var responseDTO = CommuPostResponseDTO.SuccessResponseDTO(success = false)
            when (targetType){
                TargetType.POST->{
                    if(dto is CommuPostRequestDTO.ModifyDTO){
                        TODO("dto 형식 변환")
                        responseDTO = boardService.modifyPost(dto).await()
                    }
                }
                TargetType.COMMENT->{
                    responseDTO = commentService.modifyComment(dto).await()
                }
            }
            CommuPostResponse.SuccessResponse(responseDTO)
        }
    }

}
