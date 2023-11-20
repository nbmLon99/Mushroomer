package com.nbmlon.mushroomer.api.service

import com.nbmlon.mushroomer.api.RetrofitModule
import com.nbmlon.mushroomer.api.dto.CommentRequestDTO
import com.nbmlon.mushroomer.api.dto.CommentResponseDTO
import com.nbmlon.mushroomer.api.dto.DefaultResponseDTO
import com.nbmlon.mushroomer.domain.CommuPostUseCaseResponse.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface CommentService {
    //댓글 목록 조회
    @GET("/api/comments/{articleId}")
    suspend fun getComments(
        @Path("articleId")boardId : Int
    ) : Call<CommentResponseDTO.CommentsDTO>

    //댓글 등록
    @POST("/api/comments/post/{articleId}")
    suspend fun writeComment(
        @Header("Authorization")token : String,
        @Path("articleId")articleId: Int,
        @Body dto : CommentRequestDTO.UploadCommentDTO
    ) : Call<DefaultResponseDTO>

    //댓글 수정
    @PATCH("/api/comments/edit/{articleId}")
    suspend fun editComment(
        @Header("Authorization") token : String,
        @Path("articleId")articleId : Int,
        @Body dto : CommentRequestDTO.UploadCommentDTO
    ) : Call<DefaultResponseDTO>

    //댓글 삭제
    @DELETE("/api/comments/delete/{articleid}/{commentId}")
    suspend fun deleteComment(
        @Header("Authorization")token : String,
        @Path("articleid")articleId : Int,
        @Path("commentId")commentId : Int
    ) : Call<DefaultResponseDTO>

}


class CommentServiceModule {
    val retrofit : Retrofit = RetrofitModule.getRetrofit()

    fun getCommentService(): CommentService {
        return retrofit.create(CommentService::class.java)
    }
}