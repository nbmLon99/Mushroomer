package com.nbmlon.mushroomer.data.user

import com.nbmlon.mushroomer.api.UserService
import javax.inject.Inject


fun UserRepository() : UserRepository = UserRepositoryImpl()

interface UserRepository {
}

private class UserRepositoryImpl: UserRepository {
    @Inject
    private lateinit var backend : UserService
    // 기타 서버와의 통신에 필요한 메소드들을 구현
}


