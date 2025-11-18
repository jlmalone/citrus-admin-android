package com.citrus.admin.data.repository

import com.citrus.admin.data.remote.ApiService
import com.citrus.admin.data.remote.dto.AnalyticsChartsDto
import com.citrus.admin.data.remote.dto.AnalyticsSummaryDto
import javax.inject.Inject

interface AnalyticsRepository {
    suspend fun getAnalyticsSummary(): Result<AnalyticsSummaryDto>
    suspend fun getAnalyticsCharts(period: String): Result<AnalyticsChartsDto>
}

class AnalyticsRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AnalyticsRepository {

    override suspend fun getAnalyticsSummary(): Result<AnalyticsSummaryDto> =
        try {
            Result.success(apiService.getAnalyticsSummary())
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun getAnalyticsCharts(period: String): Result<AnalyticsChartsDto> =
        try {
            Result.success(apiService.getAnalyticsCharts(period))
        } catch (e: Exception) {
            Result.failure(e)
        }
}
