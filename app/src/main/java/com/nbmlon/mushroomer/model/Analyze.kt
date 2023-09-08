package com.nbmlon.mushroomer.model

import java.io.Serializable

class Analyze{
    /** 서버 응답 결과 **/
    data class AnalyzeResponse(
        val mushroom : Mushroom
    ) : Serializable{
        val isNew = !mushroom.gotcha
        var accuracy : Int = 0

        companion object{
            fun getDummy() : AnalyzeResponse{
                return AnalyzeResponse(Mushroom.getDummy(112,false))
            }
        }
    }

    /** 서버로 보내서 머쉬룸 데이터 가져옴 **/
    data class AnalyzeRequest(
        val mushId : Int
    )
}
