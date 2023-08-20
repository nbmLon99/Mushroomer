package com.nbmlon.mushroomer.data.posts

import com.nbmlon.mushroomer.model.Post
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface PostsService {

    @GET()
    suspend fun getFreePosts(
        query: String,
        pageNumber : Int
    ) : PostsResponse

    @GET()
    suspend fun getQnAPosts(
        query: String,
        pageNumber : Int
    ) : PostsResponse

    @GET()
    suspend fun getPicPosts(
        query: String,
        pageNumber : Int
    ) : PostsResponse

    companion object {
        private const val BASE_URL = "http://127.0.0.1"

        fun create(): PostsService {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BASIC

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()
            val callFactory: okhttp3.Call.Factory = client


            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .callFactory(callFactory) // callFactory를 사용하여 OkHttpClient 인스턴스를 지정합니다.
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PostsService::class.java)
        }
    }


    @GET("/Board")
    fun getPostsByIdx(): Call<Post>



}