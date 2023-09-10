package com.nbmlon.mushroomer.api.service

import android.graphics.Bitmap
import com.nbmlon.mushroomer.api.dto.SuccessResponse
import dagger.Module
import dagger.Provides
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Url

interface ObserveService {
    //버섯 이미지 조회
    @GET("/observes/{boardId}")
    suspend fun getPostPhotos(@Path("boardId") boardId : Int) : Call<ArrayList<Url>>
    @POST("/observes/image/{boardId}")
    suspend fun uploadPostPhotos(@Path("boardId") boardId: Int, uploadFiles : ArrayList<Bitmap>) : Call<SuccessResponse>
    @PUT("/observes/image/{boardId}")
    suspend fun modifyPostPhotos(@Path("boardId") boardId: Int, uploadFiles: ArrayList<Bitmap>) : Call<SuccessResponse>
}

@Module
class ObserveServiceModule {
    @Provides
    fun provideUserService(retrofit: Retrofit): ObserveService {
        return retrofit.create(ObserveService::class.java)
    }
}
