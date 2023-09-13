package com.nbmlon.mushroomer.domain

import com.nbmlon.mushroomer.model.Mushroom
import java.io.Serializable


/** 서버 응답 결과 **/
data class AnalyzeResponse(
    val mushroom : Mushroom
) : Serializable {
    val isNew = !mushroom.gotcha
    var accuracy : Int = 0
}
