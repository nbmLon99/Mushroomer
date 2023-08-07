package com.nbmlon.mushroomer.data.dogam

import com.nbmlon.mushroomer.model.Dogam

interface DogamService {
    fun getMushes(query: String, pageNumber : Int)
}