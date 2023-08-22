package com.nbmlon.mushroomer.model

import org.joda.time.DateTime
import java.util.Date

data class MushHistory (
    val picPath : ArrayList<String>,
    val date : DateTime,
    val lat : Long,
    val lon : Long
)