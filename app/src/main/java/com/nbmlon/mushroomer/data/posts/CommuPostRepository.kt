package com.nbmlon.mushroomer.data.posts

import com.nbmlon.mushroomer.AppUser
import com.nbmlon.mushroomer.api.ResponseCodeConstants.NETWORK_ERROR_CODE
import com.nbmlon.mushroomer.api.service.BoardService
import com.nbmlon.mushroomer.api.service.CommentService
import com.nbmlon.mushroomer.api.service.ReportService
import com.nbmlon.mushroomer.domain.CommuPostUseCaseRequest.DeleteRequestDomain
import com.nbmlon.mushroomer.domain.CommuPostUseCaseRequest.LoadPostRequestDomain
import com.nbmlon.mushroomer.domain.CommuPostUseCaseRequest.ModifyCommentRequestDomain
import com.nbmlon.mushroomer.domain.CommuPostUseCaseRequest.ReportRequestDomain
import com.nbmlon.mushroomer.domain.CommuPostUseCaseRequest.UploadCommentRequestDomain
import com.nbmlon.mushroomer.domain.CommuPostUseCaseResponse
import com.nbmlon.mushroomer.domain.TargetType
import com.nbmlon.mushroomer.domain.toPostDomain
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
    suspend fun loadPost(domain: LoadPostRequestDomain) : CommuPostUseCaseResponse.PostResponseDomain
}

fun CommuPostRepository() : CommuPostRepository = CommuPostRepositoryImpl()




class CommuPostRepositoryImpl : CommuPostRepository {
    @Inject private lateinit var boardService : BoardService
    @Inject private lateinit var reportService : ReportService
    @Inject private lateinit var commentService : CommentService

    override suspend fun report(domain : ReportRequestDomain): CommuPostUseCaseResponse {
        return try{
            withContext(Dispatchers.IO){
                when (domain.type){
                    TargetType.POST->{
                        reportService.report(domain.id).await().toPostDomain()
                    }
                    TargetType.COMMENT->{
                        reportService.report(domain.id).await().toPostDomain()
                    }
                }
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
                when (domain.type){
                    TargetType.POST->{
                        boardService.deleteBoard(domain.id).await().toPostDomain()
                    }
                    TargetType.COMMENT->{
                        commentService.deleteComment(AppUser.user!!.id, domain.id).await().toPostDomain()
                    }
                }
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
                commentService.writeComment(AppUser?.user!!.id,domain.target.boardId, domain.target).await().toPostDomain()
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
                commentService.modifyComment(AppUser?.user!!.id,domain.target.id, domain.target).await().toPostDomain()
            }
        }catch (e : IOException){
            CommuPostUseCaseResponse.SuccessResponseDomain(false, NETWORK_ERROR_CODE)
        }catch (e : Exception){
            CommuPostUseCaseResponse.SuccessResponseDomain(false)
        }
    }

    override suspend fun loadPost(domain: LoadPostRequestDomain): CommuPostUseCaseResponse.PostResponseDomain {
        return try {
            withContext(Dispatchers.IO) {
                boardService.getPost(domain.id).await().toPostDomain()
            }
        }catch (e : IOException){
            CommuPostUseCaseResponse.PostResponseDomain(false, NETWORK_ERROR_CODE)
        }catch (e : Exception){
            CommuPostUseCaseResponse.PostResponseDomain(false)
        }
    }
}






