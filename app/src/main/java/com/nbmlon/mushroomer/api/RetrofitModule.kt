package com.nbmlon.mushroomer.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(ActivityComponent::class)
object RetrofitModule {
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://ec2-3-34-20-160.ap-northeast-2.compute.amazonaws.com:1000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

