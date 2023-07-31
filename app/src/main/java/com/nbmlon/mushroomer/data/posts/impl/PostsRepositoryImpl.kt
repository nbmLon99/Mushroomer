package com.nbmlon.mushroomer.data.posts.impl

import com.nbmlon.mushroomer.data.posts.PostsRepository
import com.nbmlon.mushroomer.data.posts.PostsService
import com.nbmlon.mushroomer.model.Post
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PostsRepositoryImpl private constructor(): PostsRepository {
    private val baseUrl : String = ""
    companion object {
        val instance by lazy { PostsRepositoryImpl() }
    }
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val postsService: PostsService = retrofit.create(PostsService::class.java)

    fun fetchData(): Call<Post> {
        return postsService.getPostsByIdx()
    }


}