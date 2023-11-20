package com.nbmlon.mushroomer.api.service

import com.nbmlon.mushroomer.api.RetrofitModule
import com.nbmlon.mushroomer.api.dto.MushroomResponseDTO
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path

interface MushroomService {
    @GET("/api/mushrooms")
    suspend fun getMushrooms(

    ) : Call<MushroomResponseDTO.MushesResponseDTO>
    @GET("/api/mushrooms/{mushId}")
    suspend fun getMushroom(
        @Path("mushId") mushId : Int
    ) : Call<MushroomResponseDTO.MushResponseDTO>
}

class MushServiceModule {
    val retrofit : Retrofit = RetrofitModule.getRetrofit()

    fun getMushService(): MushroomService {
        return retrofit.create(MushroomService::class.java)
    }
}
