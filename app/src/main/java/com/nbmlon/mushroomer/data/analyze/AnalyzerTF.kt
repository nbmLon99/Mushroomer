package com.nbmlon.mushroomer.data.analyze

import com.nbmlon.mushroomer.domain.AnalyzeUseCaseRequest

internal fun AnalyzerTF(domain : AnalyzeUseCaseRequest.AnalyzeRequestDomain) : ResponseTF{
    val pictures = domain
    return ResponseTF(mushId = 0, accuracy = 0)
}


data class ResponseTF(
    val mushId : Int,
    val accuracy : Int
)