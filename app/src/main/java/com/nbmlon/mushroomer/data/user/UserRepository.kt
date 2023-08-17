package com.nbmlon.mushroomer.data.user
import com.nbmlon.mushroomer.model.User
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


fun UserRepository() : UserRepository = UserRepositoryImpl()

interface UserRepository {
}

private class UserRepositoryImpl: UserRepository {
    private val baseUrl : String = ""
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


