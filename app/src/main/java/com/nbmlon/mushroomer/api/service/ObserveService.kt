package com.nbmlon.mushroomer.api.service

import android.graphics.Bitmap
import com.nbmlon.mushroomer.api.dto.DefaultResponseDTO
import com.nbmlon.mushroomer.api.dto.ObserveRequestDTO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Url

interface ObserveService {
    //발견 저장
    @Multipart
    @POST("/api/observes/save")
    suspend fun saveHistory(
        @Part images: List<MultipartBody.Part>,
        @Body dto : ObserveRequestDTO.ObserveDTO
    ) : Call<DefaultResponseDTO>
}

@Module
@InstallIn(ViewModelComponent::class)
class ObserveServiceModule {
    @Provides
    fun provideUserService(retrofit: Retrofit): ObserveService {
        return retrofit.create(ObserveService::class.java)
    }
}
