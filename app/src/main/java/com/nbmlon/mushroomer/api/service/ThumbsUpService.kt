package com.nbmlon.mushroomer.api.service

import com.nbmlon.mushroomer.api.RetrofitModule
import com.nbmlon.mushroomer.api.dto.DefaultResponseDTO
import com.nbmlon.mushroomer.api.dto.ThumbsUpRequestDTO
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST

interface ThumbsUpService {

    //좋아요 추가
    @POST("/api/likes/insert")
    suspend fun like(
        @Header("Authorization")token : String,
        @Body dto : ThumbsUpRequestDTO.ThumbsUpDTO
        ) : Call<DefaultResponseDTO>

    //좋아요 삭제
    @DELETE("/api/likes/insert")
    suspend fun dislike(
        @Header("Authorization")token : String,
        @Body dto : ThumbsUpRequestDTO.ThumbsUpDTO
        ) : Call<DefaultResponseDTO>
}

class ThumbsServiceModule {
    val retrofit : Retrofit = RetrofitModule.getRetrofit()

    fun getThumbsService(): ThumbsUpService {
        return retrofit.create(ThumbsUpService::class.java)
    }
}

