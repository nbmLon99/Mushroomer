package com.nbmlon.mushroomer.data.dogam

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.nbmlon.mushroomer.api.MushroomService
import com.nbmlon.mushroomer.model.Mushroom
import com.nbmlon.mushroomer.ui.dogam.DogamSortingOption
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface DogamRepository {
    fun getDogamstream(query : String?, sortingOption: DogamSortingOption): Flow<PagingData<Mushroom>>
}

fun DogamRepository() : DogamRepository = DogamRepositoryImpl()

private class DogamRepositoryImpl : DogamRepository {
    companion object {
        const val NETWORK_PAGE_SIZE = 50
    }

    @Inject
    private lateinit var backend : MushroomService

    override fun getDogamstream(query : String?, sortingOption: DogamSortingOption): Flow<PagingData<Mushroom>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { DogamPagingSource( backend, query, sortingOption) }
        ).flow
    }
}