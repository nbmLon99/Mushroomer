package com.nbmlon.mushroomer.api.service

import com.nbmlon.mushroomer.api.RetrofitModule
import com.nbmlon.mushroomer.api.dto.BoardRequestDTO
import com.nbmlon.mushroomer.api.dto.BoardResponseDTO.*
import com.nbmlon.mushroomer.api.dto.DefaultResponseDTO
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path


interface BoardService {

    //특정 게시글 조회
    @GET("/api/boards/{articleId}")
    suspend fun getPost(
        @Path("articleId")id : Int
    ) : Call<PostResponseDTO>

    //게시판별 게시글 조회
    @GET("/api/boards/type/{type}")
    suspend fun getBoardPosts(
        @Path("type") type : String
    ) : Call<PostsResponseDTO>


    //게시글 작성
    @Multipart
    @POST("/api/boards/post")
    suspend fun writePost(
        @Header("Authorization")token : String,
        @Body dto : BoardRequestDTO.PostRequestDTO,
        @Part images: List<MultipartBody.Part>
    ) : Call<DefaultResponseDTO>

    //유저 게시글 조회
    @GET("/api/boards/users")
    suspend fun getUserPosts(
        @Header("Authorization")token : String,
    ) :  Call<PostsResponseDTO>

    //게시글 수정
    @Multipart
    @PATCH("/api/boards/edit/{articleId}")
    suspend fun modifyPost(
        @Path("articleId") id: Int,
        @Body dto : BoardRequestDTO.PostRequestDTO,
        @Part images: List<MultipartBody.Part>
    ) : Call<DefaultResponseDTO>

    //게시글 삭제
    @DELETE("/api/boards/delete/{articleId}")
    suspend fun deleteBoard(
        @Header("Authorization")token : String,
        @Path("articleId")id : Int
    ) : Call<DefaultResponseDTO>

    //게시글 키워드(제목)조회
    @GET("/api/boards/search/{keyword}")
    suspend fun searchBoard(
        @Path("keyword") keyword : String
    ) : Call<PostsResponseDTO>

    //게시글 정렬(좋아요수,작성날짜 수)
    @GET("/api/boards/sort/{sort}/{limit}")
    suspend fun getRecentBoard(
        @Path("limit") limit : Int = 5,
        @Path("sort") sort : String = "update_time"
    ) : Call<PostsResponseDTO>
}



class BoardServiceModule {
    val retrofit : Retrofit = RetrofitModule.getRetrofit()

    fun getBoardService(): BoardService {
        return retrofit.create(BoardService::class.java)
    }
}