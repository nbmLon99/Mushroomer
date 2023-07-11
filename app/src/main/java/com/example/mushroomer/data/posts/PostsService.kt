package com.example.mushroomer.data.posts

import android.telecom.Call
import com.example.mushroomer.model.Post
import retrofit2.http.GET

interface PostsService {
    @GET("/Board")
    fun getPostsByIdx(): Call<Post>

    

}