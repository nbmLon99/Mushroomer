package com.nbmlon.mushroomer.data.dogam.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.nbmlon.mushroomer.data.dogam.DogamPagingSource
import com.nbmlon.mushroomer.data.dogam.DogamRepository
import com.nbmlon.mushroomer.data.dogam.DogamService
import com.nbmlon.mushroomer.model.Mushroom
import kotlinx.coroutines.flow.Flow


class DogamRepositoryImpl : DogamRepository {
    companion object {
        const val NETWORK_PAGE_SIZE = 50
    }


    override fun getDogamstream(query: String): Flow<PagingData<Mushroom>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { DogamPagingSource(DogamService.create(), query) }
        ).flow
    }



}