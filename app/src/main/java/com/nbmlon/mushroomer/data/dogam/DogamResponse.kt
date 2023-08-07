package com.nbmlon.mushroomer.data.dogam

import com.google.gson.annotations.SerializedName
import com.nbmlon.mushroomer.model.Mushroom

data class DogamResponse(
    @SerializedName("") val percent : Int = 0,
    @SerializedName("") val items : ArrayList<Mushroom> = ArrayList(),
    val nextPage : Int? = null
)

