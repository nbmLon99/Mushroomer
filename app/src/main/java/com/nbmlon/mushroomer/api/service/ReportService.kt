package com.nbmlon.mushroomer.api.service

import com.nbmlon.mushroomer.api.dto.ReportResponseDTO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.POST
import retrofit2.http.Path

interface ReportService {
    @POST("/reports/{reportId}")
    suspend fun report(@Path("reportId")id: Int) : Call<ReportResponseDTO.SuccessResponseDTO>
}

@Module
@InstallIn(ViewModelComponent::class)
class ReportServiceModule {
    @Provides
    fun provideReportService(retrofit: Retrofit): ReportService {
        return retrofit.create(ReportService::class.java)
    }
}
