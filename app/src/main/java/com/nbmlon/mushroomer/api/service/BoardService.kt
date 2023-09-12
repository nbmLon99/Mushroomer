package com.nbmlon.mushroomer.api.service

import com.google.gson.annotations.SerializedName
import com.nbmlon.mushroomer.api.dto.BoardResponseDTO.*
import com.nbmlon.mushroomer.model.Comment
import com.nbmlon.mushroomer.model.Post
import dagger.Module
import dagger.Provides
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface BoardService {
    //게시판별 게시글 조회
    @GET("/boards/type/{type}")
    suspend fun getBoardPosts(query : String?, @Path("type") type : String) : Call<PostsArrayResponseDTO>

    //특정 개시글 조회
    @GET("/boards/{boardId}")
    suspend fun getPost(@Path("boardId")id : Int) : Call<PostResponseDTO>

    //
    @POST("/boards/{userId}")
    suspend fun writePost(@Path("userId")id : Int, post : Post) : Call<SuccessResponseDTO>

    //유저 게시글 조회
    @GET("/boards/users/{userId}")
    suspend fun getUserPosts(@Path("userId")id:Int) :  Call<PostsArrayResponseDTO>

    //게시글 수정
    @PUT("/boards/{userId}/{boardId}")
    suspend fun modifyPost(@Path("userId")userId: Int, @Path("boardId")boardId : Int, post : Post) : Call<SuccessResponseDTO>

    //게시글 삭제
    @DELETE("/boards/{userId}/{boardId}")
    suspend fun deleteBoard(@Path("boardId")id : Int) : Call<SuccessResponseDTO>
}



@Module
class BoardServiceModule {
    @Provides
    fun provideBoardService(retrofit: Retrofit): BoardService {
        return retrofit.create(BoardService::class.java)
    }
}

data class BoardResponse(
    @SerializedName("data") val items: ArrayList<Post>,
    @SerializedName("message") val message: String?
)

data class BoardData(
    @SerializedName("createdDate") val createdDate: String,
    @SerializedName("updatedDate") val updatedDate: String,
    @SerializedName("boardId") val boardId: Int,
    @SerializedName("type") val type: String,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("status") val status: String,
    @SerializedName("image") val image: String,
    @SerializedName("comments") val comments: List<Comment>,
    @SerializedName("thumbsUps") val thumbsUps: Int
)

