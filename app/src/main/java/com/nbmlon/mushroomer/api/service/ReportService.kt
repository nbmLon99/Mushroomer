package com.nbmlon.mushroomer.api.service

import com.nbmlon.mushroomer.api.dto.DefaultResponseDTO
import com.nbmlon.mushroomer.api.dto.ReportRequestDTO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ReportService {
    @POST("/reports/{reportId}")
    suspend fun report(
        @Header("x-access-token")token : String,
        @Path("reportId")id: Int,
        @Body dto : ReportRequestDTO.ReportDTO
    ) : Call<DefaultResponseDTO>
}

@Module
@InstallIn(ViewModelComponent::class)
class ReportServiceModule {
    @Provides
    fun provideReportService(retrofit: Retrofit): ReportService {
        return retrofit.create(ReportService::class.java)
    }
}
