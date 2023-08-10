package com.nbmlon.mushroomer.data.dogam

import com.nbmlon.mushroomer.model.Dogam
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface DogamService {

    @GET()
    suspend fun getDogam(
        query: String,
        pageNumber : Int
    ) : DogamResponse

    companion object {
        private const val BASE_URL = "http://127.0.0.1"

        fun create(): DogamService {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BASIC

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DogamService::class.java)
        }
    }
}