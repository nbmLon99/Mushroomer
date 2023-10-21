package com.nbmlon.mushroomer.data.dogam

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.nbmlon.mushroomer.api.ResponseCodeConstants.NETWORK_ERROR_CODE
import com.nbmlon.mushroomer.api.service.MushroomService
import com.nbmlon.mushroomer.domain.DogamUseCaseReqeust
import com.nbmlon.mushroomer.domain.DogamUseCaseResponse
import com.nbmlon.mushroomer.domain.toDogamDomain
import com.nbmlon.mushroomer.model.Mushroom
import com.nbmlon.mushroomer.ui.dogam.DogamSortingOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.await
import java.io.IOException
import javax.inject.Inject

interface DogamRepository {
    suspend fun fetchDogam() : DogamUseCaseResponse.LoadDogamResponse
    fun getDogamstream(domain : DogamUseCaseReqeust.LoadDogamResquestDomain, items : List<Mushroom>): Flow<PagingData<Mushroom>>
    suspend fun getSpecificDogam(domain : DogamUseCaseReqeust.SpecificDogamRequestDomain) : DogamUseCaseResponse.SpecificDogamResponse
}

fun DogamRepository() : DogamRepository = DogamRepositoryImpl()

private class DogamRepositoryImpl : DogamRepository {
    companion object {
        const val NETWORK_PAGE_SIZE = 50
    }

    @Inject
    lateinit var backend : MushroomService
    override suspend fun fetchDogam(): DogamUseCaseResponse.LoadDogamResponse {
        return withContext(Dispatchers.IO){
            backend.getMushrooms().await().toDogamDomain()
        }
    }

    override fun getDogamstream(domain : DogamUseCaseReqeust.LoadDogamResquestDomain, items : List<Mushroom>): Flow<PagingData<Mushroom>> {
        val filteredItems = items.filter { it.name.contains(domain.query.orEmpty(), ignoreCase = true) }
            .sortedWith(compareBy(
                { it.name },
                { it.dogamNo },
                { it.rarity }
            ))

        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { DogamPagingSource( filteredItems )}
        ).flow
    }

    override suspend fun getSpecificDogam(domain: DogamUseCaseReqeust.SpecificDogamRequestDomain): DogamUseCaseResponse.SpecificDogamResponse {
        return try {
            withContext(Dispatchers.IO){
                backend.getMushroom(domain.id).await().toDogamDomain()
            }
        }catch (e : IOException){
            DogamUseCaseResponse.SpecificDogamResponse(false, NETWORK_ERROR_CODE)
        }catch (e : Exception){
            DogamUseCaseResponse.SpecificDogamResponse(false)
        }
    }



    suspend fun fetchData(): DogamUseCaseResponse.LoadDogamResponse {
        return withContext(Dispatchers.IO) {
            backend.getMushrooms().await().toDogamDomain()
        }
    }
}