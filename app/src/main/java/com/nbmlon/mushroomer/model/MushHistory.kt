package com.nbmlon.mushroomer.model

import org.joda.time.DateTime
import java.io.Serializable
import java.util.Date

data class MushHistory (
    val mushroom : Mushroom,
    val picPath : ArrayList<String>,
    val date : DateTime,
    val lat : Long,
    val lon : Long
) : Serializable