package com.nbmlon.mushroomer.data.dogam

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.nbmlon.mushroomer.api.ResponseCodeConstants.NETWORK_ERROR_CODE
import com.nbmlon.mushroomer.api.ResponseCodeConstants.SUCCESS_CODE
import com.nbmlon.mushroomer.api.ResponseCodeConstants.UNDEFINED_ERROR_CODE
import com.nbmlon.mushroomer.api.service.MushServiceModule
import com.nbmlon.mushroomer.api.service.MushroomService
import com.nbmlon.mushroomer.domain.DogamUseCaseReqeust
import com.nbmlon.mushroomer.domain.DogamUseCaseResponse
import com.nbmlon.mushroomer.model.MushType
import com.nbmlon.mushroomer.model.Mushroom
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

    val mushService : MushroomService = MushServiceModule().getMushService()
    override suspend fun fetchDogam(): DogamUseCaseResponse.LoadDogamResponse {
        return try{
            withContext(Dispatchers.IO){
                val result = mushService.getMushrooms().await()
                DogamUseCaseResponse.LoadDogamResponse(
                    true,
                    SUCCESS_CODE,
                    result.data.map { Mushroom(
                        dogamNo = it.id,
                        imageUrl = it.imageUrl,
                        name = it.name,
                        feature = it.feature,
                        type = if(it.type == "EDIBLE") MushType.EDIBLE else MushType.POISON,
                        rarity = it.rarity,
                        myHistory = listOf(),
                        gotcha = it.gotcha=="true"
                    ) }
                )
            }
        }catch (e :IOException){
            DogamUseCaseResponse.LoadDogamResponse(false, NETWORK_ERROR_CODE, listOf())
        }catch (e :Exception){
            DogamUseCaseResponse.LoadDogamResponse(false, UNDEFINED_ERROR_CODE, listOf())
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
                val result = mushService.getMushroom(domain.id).await()
                DogamUseCaseResponse.SpecificDogamResponse(
                    true,
                    SUCCESS_CODE,
                    mush = Mushroom(
                        dogamNo = result.data.id,
                        imageUrl = result.data.imageUrl,
                        name =result.data.name,
                        feature = result.data.feature,
                        type = if(result.data.type == "EDIBLE") MushType.EDIBLE else MushType.POISON,
                        rarity = result.data.rarity,
                        myHistory = listOf(),
                        gotcha = result.data.gotcha =="true"
                    ),
                    history = null,

                )
            }
        }catch (e : IOException){
            DogamUseCaseResponse.SpecificDogamResponse(false, NETWORK_ERROR_CODE)
        }catch (e : Exception){
            DogamUseCaseResponse.SpecificDogamResponse(false)
        }
    }
}