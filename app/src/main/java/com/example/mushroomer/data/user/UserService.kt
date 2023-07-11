package com.example.mushroomer.data.user

import retrofit2.Call
import com.example.mushroomer.model.User
import retrofit2.http.GET

interface UserService {
    @GET("/User")
    fun getUserByID(): Call<User>

}