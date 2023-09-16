package com.nbmlon.mushroomer.data.marker

import com.nbmlon.mushroomer.domain.MapUseCaseResponse


fun HistoryRepository() : HistoryRepository = HistoryRepositoryImpl()

interface HistoryRepository {
    suspend fun getHistories() : MapUseCaseResponse.MapResponseDomain
}

class HistoryRepositoryImpl : HistoryRepository{
    //@Inject
    // lateinit var repository :
    override suspend fun getHistories(): MapUseCaseResponse.MapResponseDomain {
        TODO("Not yet implemented")
    }

}