package com.nbmlon.mushroomer.data.posts

import com.nbmlon.mushroomer.api.dto.CommuRequestDTO
import com.nbmlon.mushroomer.ui.commu.post.CommuResponse
import com.nbmlon.mushroomer.ui.commu.post.TargetType


/** 특정 게시판 열린 상태에서 사용할 repo **/
interface CommuPostRepository {
    suspend fun report(targetType : TargetType, dto : CommuRequestDTO) : CommuResponse
    suspend fun delete(targetType : TargetType, dto : CommuRequestDTO) : CommuResponse
    suspend fun upload(targetType : TargetType, dto : CommuRequestDTO) : CommuResponse
    suspend fun modify(targetType : TargetType, dto : CommuRequestDTO) : CommuResponse
}

fun CommuPostRepository() : CommuPostRepository = CommuPostRepositoryImpl()




class CommuPostRepositoryImpl : CommuPostRepository {

    override suspend fun report(targetType: TargetType, dto: CommuRequestDTO): CommuResponse {
        TODO("Not yet implemented")
    }

    override suspend fun delete(targetType: TargetType, dto: CommuRequestDTO): CommuResponse {
        TODO("Not yet implemented")
    }

    override suspend fun upload(targetType: TargetType, dto: CommuRequestDTO): CommuResponse {
        TODO("Not yet implemented")
    }

    override suspend fun modify(targetType: TargetType, dto: CommuRequestDTO): CommuResponse {
        TODO("Not yet implemented")
    }

}
