package com.example.mushroomer.data.user.impl

import com.example.mushroomer.data.user.UserRepository
import com.example.mushroomer.data.user.UserService
import com.example.mushroomer.model.User
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserRepositoryImpl private constructor(): UserRepository {
    private val baseUrl : String = ""
    companion object{
        val instance by lazy{ UserRepositoryImpl() }
    }

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val userService: UserService = retrofit.create(UserService::class.java)

    fun fetchData(): Call<User> {
        return userService.getUserByID()
    }

    // 기타 서버와의 통신에 필요한 메소드들을 구현
}
