package com.nbmlon.mushroomer.api.service

import com.nbmlon.mushroomer.api.dto.MushroomResponseDTO
import com.nbmlon.mushroomer.ui.dogam.DogamSortingOption
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path

interface MushroomService {
    @GET("/mushrooms")
    suspend fun getMushrooms() : Call<MushroomResponseDTO.MushesResponseDTO>
    @GET("/mushrooms/{mushId}")
    suspend fun getMushroom(@Path("mushId") mushId : Int) : Call<MushroomResponseDTO.MushResponseDTO>
}

@Module
@InstallIn(ViewModelComponent::class)
class MushServiceModule {
    @Provides
    fun provideMushService(retrofit: Retrofit): MushroomService {
        return retrofit.create(MushroomService::class.java)
    }
}
