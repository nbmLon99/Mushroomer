package com.nbmlon.mushroomer.api

import com.google.gson.annotations.SerializedName
import com.nbmlon.mushroomer.model.Mushroom
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface DogamService {

    @GET()
    suspend fun getDogam(
        query: String?
    ) : DogamResponse

    companion object {
        private const val BASE_URL = "http://127.0.0.1"

        fun create(): DogamService {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BASIC

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()
            val callFactory: Call.Factory = client

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .callFactory(callFactory) // callFactory를 사용하여 OkHttpClient 인스턴스를 지정합니다.
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DogamService::class.java)
        }
    }
}



data class DogamResponse(
    @SerializedName("items") val items : ArrayList<Mushroom> = ArrayList(),
)

