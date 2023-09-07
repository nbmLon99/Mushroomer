package com.nbmlon.mushroomer.api

import com.nbmlon.mushroomer.model.Mushroom
import dagger.Module
import dagger.Provides
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path

interface MushroomService {
    @GET("/mushrooms")
    suspend fun getMushrooms(query : String?) : Call<DogamResponse>
    @GET("/mushrooms/{mushId}")
    suspend fun getMushroom(@Path("mushId") mushId : Int) : Call<Mushroom>
}

@Module
class MushServiceModule {
    @Provides
    fun provideMushService(retrofit: Retrofit): MushroomService {
        return retrofit.create(MushroomService::class.java)
    }
}
