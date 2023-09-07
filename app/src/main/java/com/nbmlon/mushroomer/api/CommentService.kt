package com.nbmlon.mushroomer.api

import com.google.gson.annotations.SerializedName
import com.nbmlon.mushroomer.model.Comment
import dagger.Module
import dagger.Provides
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CommentService {
    //댓글 목록 조회
    @GET("/comments/{boardId}")
    suspend fun getComments(@Path("boardId")boardId : Int)

    //댓글 등록
    @POST("/comments/{userId}/{boardId}")
    suspend fun writeComment(@Path("userId")userId : Int, @Path("boardId")boardId : Int, comment: Comment) : Call<SuccessResponse>

    //댓글 수정
    @PUT("/comments/{userId}/{boardId}")
    suspend fun modifyComment(@Path("userId")userId : Int, @Path("boardId")boardId : Int, comment: Comment) : Call<SuccessResponse>

    //댓글 삭제
    @DELETE("/comments/{userid}/{boardId}")
    suspend fun deleteComment(@Path("userId")userId : Int, @Path("boardId")boardId : Int) : Call<SuccessResponse>

}


@Module
class CommentServiceModule {
    @Provides
    fun provideCommentService(retrofit: Retrofit): CommentService {
        return retrofit.create(CommentService::class.java)
    }
}


data class CommentResponse(
    @SerializedName("data") val data: ArrayList<Comment>,
    @SerializedName("message") val message: String?
)