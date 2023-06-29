package com.example.mushroomer.data.posts.impl

import com.example.mushroomer.data.posts.PostsRepository
import com.example.mushroomer.data.posts.PostsService
import com.example.mushroomer.data.user.UserService
import com.example.mushroomer.model.Post
import com.example.mushroomer.model.User
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PostRepositoryImpl private constructor(): PostsRepository {
    private val baseUrl : String = ""
    companion object {
        val instance by lazy { PostRepositoryImpl() }
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