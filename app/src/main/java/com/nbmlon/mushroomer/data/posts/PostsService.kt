package com.nbmlon.mushroomer.data.posts

import retrofit2.Call
import com.nbmlon.mushroomer.model.Post
import retrofit2.http.GET

interface PostsService {
    @GET("/Board")
    fun getPostsByIdx(): Call<Post>



}