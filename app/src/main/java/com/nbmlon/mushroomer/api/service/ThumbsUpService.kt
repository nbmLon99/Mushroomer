package com.nbmlon.mushroomer.api.service

import com.nbmlon.mushroomer.api.dto.DefaultResponseDTO
import com.nbmlon.mushroomer.api.dto.ThumbsUpRequestDTO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
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

@Module
@InstallIn(ViewModelComponent::class)
class ThumbsServiceModule {
    @Provides
    fun provideThumbsService(retrofit: Retrofit): ThumbsUpService {
        return retrofit.create(ThumbsUpService::class.java)
    }
}

