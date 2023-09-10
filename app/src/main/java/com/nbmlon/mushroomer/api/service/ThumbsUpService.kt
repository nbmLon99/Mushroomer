package com.nbmlon.mushroomer.api.service

import com.nbmlon.mushroomer.api.dto.SuccessResponse
import dagger.Module
import dagger.Provides
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.DELETE
import retrofit2.http.POST

interface ThumbsUpService {

    //좋아요 추가
    @POST("")
    suspend fun ThumbsUp() : Call<SuccessResponse>

    //좋아요 삭제
    @DELETE("")
    suspend fun ThumbsDown() : Call<SuccessResponse>
}

@Module
class ThumbsServiceModule {
    @Provides
    fun provideThumbsService(retrofit: Retrofit): ThumbsUpService {
        return retrofit.create(ThumbsUpService::class.java)
    }
}

