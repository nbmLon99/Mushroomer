package com.nbmlon.mushroomer.data.dogam

import androidx.paging.PagingData
import com.nbmlon.mushroomer.model.Mushroom
import kotlinx.coroutines.flow.Flow

interface DogamRepository {
    fun getDogamstream(query: String): Flow<PagingData<Mushroom>>
}