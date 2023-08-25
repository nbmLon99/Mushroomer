package com.nbmlon.mushroomer.api

import com.google.gson.annotations.SerializedName
import com.nbmlon.mushroomer.model.Mushroom

data class DogamResponse(
    @SerializedName("items") val items : ArrayList<Mushroom> = ArrayList(),
    val nextPage : Int? = null
)

