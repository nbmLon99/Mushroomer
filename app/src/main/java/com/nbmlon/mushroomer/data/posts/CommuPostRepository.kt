package com.nbmlon.mushroomer.data.posts

import com.nbmlon.mushroomer.AppUser
import com.nbmlon.mushroomer.api.ResponseCodeConstants.NETWORK_ERROR_CODE
import com.nbmlon.mushroomer.api.ResponseCodeConstants.SUCCESS_CODE
import com.nbmlon.mushroomer.api.dto.CommentRequestDTO
import com.nbmlon.mushroomer.api.dto.DefaultResponseDTO
import com.nbmlon.mushroomer.api.dto.ThumbsUpRequestDTO
import com.nbmlon.mushroomer.api.service.BoardService
import com.nbmlon.mushroomer.api.service.BoardServiceModule
import com.nbmlon.mushroomer.api.service.CommentService
import com.nbmlon.mushroomer.api.service.CommentServiceModule
import com.nbmlon.mushroomer.api.service.ReportService
import com.nbmlon.mushroomer.api.service.ReportServiceModule
import com.nbmlon.mushroomer.api.service.ThumbsServiceModule
import com.nbmlon.mushroomer.api.service.ThumbsUpService
import com.nbmlon.mushroomer.domain.CommuPostUseCaseRequest
import com.nbmlon.mushroomer.domain.CommuPostUseCaseRequest.DeleteRequestDomain
import com.nbmlon.mushroomer.domain.CommuPostUseCaseRequest.LoadPostRequestDomain
import com.nbmlon.mushroomer.domain.CommuPostUseCaseRequest.ModifyCommentRequestDomain
import com.nbmlon.mushroomer.domain.CommuPostUseCaseRequest.ReportRequestDomain
import com.nbmlon.mushroomer.domain.CommuPostUseCaseRequest.UploadCommentRequestDomain
import com.nbmlon.mushroomer.domain.CommuPostUseCaseResponse
import com.nbmlon.mushroomer.domain.TargetType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await
import java.io.IOException
import javax.inject.Inject


/** 특정 게시판 열린 상태에서 사용할 repo **/
interface CommuPostRepository {
    suspend fun report(domain : ReportRequestDomain) : CommuPostUseCaseResponse
    suspend fun delete(domain: DeleteRequestDomain) : CommuPostUseCaseResponse
    suspend fun uploadComment(domain: UploadCommentRequestDomain) : CommuPostUseCaseResponse
    suspend fun modifyComment(domain: ModifyCommentRequestDomain) : CommuPostUseCaseResponse
    suspend fun changeThumbsUpState(domain: CommuPostUseCaseRequest.ChangeThumbsUpRequestDomain) : CommuPostUseCaseResponse
    suspend fun loadPost(domain: LoadPostRequestDomain) : CommuPostUseCaseResponse.PostResponseDomain
}

fun CommuPostRepository() : CommuPostRepository = CommuPostRepositoryImpl()




class CommuPostRepositoryImpl : CommuPostRepository {
    val boardService : BoardService = BoardServiceModule().getBoardService()
    val reportService : ReportService = ReportServiceModule().getReportService()
    val commentService : CommentService = CommentServiceModule().getCommentService()
    val thumbsUpService: ThumbsUpService = ThumbsServiceModule().getThumbsService()

    override suspend fun report(domain : ReportRequestDomain): CommuPostUseCaseResponse {
        return try{
            withContext(Dispatchers.IO){
                val result = when (domain.type){
                    TargetType.POST->{
                        //reportService.report(AppUser.token!!,domain.id,).await()
                    }
                    TargetType.COMMENT->{
                        //reportService.report(domain.id).await()
                    }
                }
                CommuPostUseCaseResponse.SuccessResponseDomain(
                    true, SUCCESS_CODE, ""
                )
            }
        }catch (e : IOException){
            CommuPostUseCaseResponse.SuccessResponseDomain(false, NETWORK_ERROR_CODE)
        }catch (e : Exception){
            CommuPostUseCaseResponse.SuccessResponseDomain(false)
        }
    }

    override suspend fun delete(domain: DeleteRequestDomain): CommuPostUseCaseResponse {
        return try{
            withContext(Dispatchers.IO){
                val result = when (domain.type){
                    TargetType.POST->{
                        boardService.deleteBoard(AppUser.token!!, domain.articleId).await()
                    }
                    TargetType.COMMENT->{
                        commentService.deleteComment(AppUser.token!!, domain.articleId, domain.commentId!!).await()
                    }
                }
                CommuPostUseCaseResponse.SuccessResponseDomain(
                    true, result.code, result.message
                )
            }
        }catch (e : IOException){
            CommuPostUseCaseResponse.SuccessResponseDomain(false, NETWORK_ERROR_CODE)
        }catch (e : Exception){
            CommuPostUseCaseResponse.SuccessResponseDomain(false)
        }
    }

    override suspend fun uploadComment(domain: UploadCommentRequestDomain): CommuPostUseCaseResponse {
        return try{
            withContext(Dispatchers.IO){
                val result = commentService.writeComment(AppUser?.token!!,domain.articleId, CommentRequestDTO.UploadCommentDTO(domain.target.content,domain.target.parentId)).await()
                CommuPostUseCaseResponse.SuccessResponseDomain(true, result.code, result.message)
            }

        }catch (e : IOException){
            CommuPostUseCaseResponse.SuccessResponseDomain(false, NETWORK_ERROR_CODE)
        }catch (e : Exception){
            CommuPostUseCaseResponse.SuccessResponseDomain(false)
        }
    }

    override suspend fun modifyComment(domain: ModifyCommentRequestDomain): CommuPostUseCaseResponse {
        return try{
            withContext(Dispatchers.IO){
                val result = commentService.editComment(AppUser.token!!, domain.articleId,
                    CommentRequestDTO.UploadCommentDTO(domain.target.content,domain.target.parentId)
                ).await()
                CommuPostUseCaseResponse.SuccessResponseDomain(true, result.code, result.message)
            }
        }catch (e : IOException){
            CommuPostUseCaseResponse.SuccessResponseDomain(false, NETWORK_ERROR_CODE)
        }catch (e : Exception){
            CommuPostUseCaseResponse.SuccessResponseDomain(false)
        }
    }

    override suspend fun changeThumbsUpState(domain: CommuPostUseCaseRequest.ChangeThumbsUpRequestDomain): CommuPostUseCaseResponse {
        return try {
            val result : DefaultResponseDTO = if(domain.toLike){
                thumbsUpService.like(AppUser.token!!, ThumbsUpRequestDTO.ThumbsUpDTO(domain.articleId)).await()
            }else{
                thumbsUpService.dislike(AppUser.token!!, ThumbsUpRequestDTO.ThumbsUpDTO(domain.articleId)).await()
            }
            CommuPostUseCaseResponse.SuccessResponseDomain(success = true,code = result.code, message = result.message)
        }catch (e : IOException){
            CommuPostUseCaseResponse.SuccessResponseDomain(false, NETWORK_ERROR_CODE)
        }catch (e : Exception){
            CommuPostUseCaseResponse.SuccessResponseDomain(false)
        }
    }

    override suspend fun loadPost(domain: LoadPostRequestDomain): CommuPostUseCaseResponse.PostResponseDomain {
        return try {
            withContext(Dispatchers.IO) {
                val result = boardService.getPost(domain.id).await()
                CommuPostUseCaseResponse.PostResponseDomain(
                    success = true, code = SUCCESS_CODE, post = result.post.toPost()
                )
            }
        }catch (e : IOException){
            CommuPostUseCaseResponse.PostResponseDomain(false, NETWORK_ERROR_CODE)
        }catch (e : Exception){
            CommuPostUseCaseResponse.PostResponseDomain(false)
        }
    }
}






