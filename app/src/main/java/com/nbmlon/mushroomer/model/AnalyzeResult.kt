package com.nbmlon.mushroomer.model

import java.io.Serializable

/** 서버 응답 결과 **/
data class AnalyzeResult(
    val mushroom : Mushroom,
    val isNew : Boolean,
    val accuracy: Int
) : Serializable