package com.nbmlon.mushroomer.api.service

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.http.POST
import retrofit2.http.Path

interface ReportService {
    @POST("/reports/{reportId}")
    suspend fun report(@Path("reportId")id: Int)
}

@Module
class ReportServiceModule {
    @Provides
    fun provideReportService(retrofit: Retrofit): ReportService {
        return retrofit.create(ReportService::class.java)
    }
}
