package com.nbmlon.mushroomer.api.service

import com.nbmlon.mushroomer.api.dto.BoardResponseDTO.*
import com.nbmlon.mushroomer.model.Post
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Multipart
import retrofit2.http.Part


interface BoardService {
    //게시판별 게시글 조회
    @GET("/boards/type/{type}")
    suspend fun getBoardPosts(query : String?, @Path("type") type : String) : Call<PostsArrayResponseDTO>

    //특정 개시글 조회
    @GET("/boards/{boardId}")
    suspend fun getPost(@Path("boardId")id : Int) : Call<PostResponseDTO>

    //게시글 작성
    @Multipart
    @POST("/boards/{userId}")
    suspend fun writePost(@Path("userId")id : Int, post : Post, @Part images: List<MultipartBody.Part>) : Call<SuccessResponseDTO>

    //유저 게시글 조회
    @GET("/boards/users/{userId}")
    suspend fun getUserPosts(@Path("userId")id:Int) :  Call<PostsArrayResponseDTO>

    //게시글 수정
    @Multipart
    @PUT("/boards/{userId}/{boardId}")
    suspend fun modifyPost(@Path("userId")userId: Int, @Path("boardId")boardId : Int, post : Post, @Part images: List<MultipartBody.Part>) : Call<SuccessResponseDTO>

    //게시글 삭제
    @DELETE("/boards/{userId}/{boardId}")
    suspend fun deleteBoard(@Path("boardId")id : Int) : Call<SuccessResponseDTO>
}



@Module
@InstallIn(ViewModelComponent::class)
class BoardServiceModule {
    @Provides
    fun provideBoardService(retrofit: Retrofit): BoardService {
        return retrofit.create(BoardService::class.java)
    }
}