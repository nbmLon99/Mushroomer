package com.nbmlon.mushroomer.data.analyze

import com.nbmlon.mushroomer.api.ResponseCodeConstants.LOW_ACCURACY_ERROR
import com.nbmlon.mushroomer.api.ResponseCodeConstants.NETWORK_ERROR_CODE
import com.nbmlon.mushroomer.api.dto.ObserveRequestDTO
import com.nbmlon.mushroomer.api.service.MushServiceModule
import com.nbmlon.mushroomer.api.service.MushroomService
import com.nbmlon.mushroomer.api.service.ObserveService
import com.nbmlon.mushroomer.api.service.ObserveServiceModule
import com.nbmlon.mushroomer.domain.AnalyzeUseCaseRequest
import com.nbmlon.mushroomer.domain.AnalyzeUseCaseResponse
import com.nbmlon.mushroomer.domain.toAnalyzeDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await
import java.io.IOException

fun AnalyzeRepository() :AnalyzeRepository = AnalyzeRepositoryImpl()

interface AnalyzeRepository{
    suspend fun analyze(domain : AnalyzeUseCaseRequest.AnalyzeRequestDomain) : AnalyzeUseCaseResponse
    suspend fun saveHistory(domain : AnalyzeUseCaseRequest.SaveHistoryDomain) : AnalyzeUseCaseResponse
}

private class AnalyzeRepositoryImpl : AnalyzeRepository {
    val mushService : MushroomService = MushServiceModule().getMushService()
    val observeService : ObserveService = ObserveServiceModule().getObserveService()

    override suspend fun analyze(domain: AnalyzeUseCaseRequest.AnalyzeRequestDomain): AnalyzeUseCaseResponse {
        return try {
            val result = AnalyzerTF(domain).getResponseTF()
            if(result == null){
                throw Exception("결과값 null")
            }else if(result!!.accuracy * 100 <= 60 )
                AnalyzeUseCaseResponse.SuccessResponseDomain(false,LOW_ACCURACY_ERROR)
            else{
                val mush = withContext(Dispatchers.IO){
                    mushService.getMushroom(result.mushId).await().toAnalyzeDomain()
                }
                mush
            }
        }catch (e : IOException){
            AnalyzeUseCaseResponse.SuccessResponseDomain(false, NETWORK_ERROR_CODE)
        }catch (e : Exception){
            AnalyzeUseCaseResponse.SuccessResponseDomain(false)
        }
    }

    override suspend fun saveHistory(domain: AnalyzeUseCaseRequest.SaveHistoryDomain): AnalyzeUseCaseResponse {
        return try {
            withContext(Dispatchers.IO){
                val dto = ObserveRequestDTO.ObserveDTO(
                    mushId = domain.mushHistory.mushroom.dogamNo, lng = domain.mushHistory.lon?.toFloat(), lat = domain.mushHistory.lat?.toFloat()
                )
                val result = observeService.saveHistory(domain.images, dto).await()
                AnalyzeUseCaseResponse.SuccessResponseDomain(
                    success = result.code != -1,
                    code = result.code,
                )
            }
        }catch (e : IOException){
            AnalyzeUseCaseResponse.SuccessResponseDomain(false, NETWORK_ERROR_CODE)
        }catch (e : Exception){
            AnalyzeUseCaseResponse.SuccessResponseDomain(false)
        }
    }
}