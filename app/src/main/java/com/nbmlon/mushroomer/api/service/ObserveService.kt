package com.nbmlon.mushroomer.api.service

import com.nbmlon.mushroomer.api.RetrofitModule
import com.nbmlon.mushroomer.api.dto.DefaultResponseDTO
import com.nbmlon.mushroomer.api.dto.ObserveRequestDTO
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ObserveService {
    //발견 저장
    @Multipart
    @POST("/api/observes/save")
    suspend fun saveHistory(
        @Part images: List<MultipartBody.Part>,
        @Body dto : ObserveRequestDTO.ObserveDTO
    ) : Call<DefaultResponseDTO>
}


class ObserveServiceModule {
    val retrofit : Retrofit = RetrofitModule.getRetrofit()

    fun getObserveService(): ObserveService {
        return retrofit.create(ObserveService::class.java)
    }
}
