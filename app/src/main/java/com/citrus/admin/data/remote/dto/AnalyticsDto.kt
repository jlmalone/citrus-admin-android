package com.citrus.admin.data.remote.dto

data class AnalyticsSummaryDto(
    val totalUsers: Int,
    val activeUsers: Int,
    val totalReceipts: Int,
    val pendingReceipts: Int,
    val approvedReceipts: Int,
    val rejectedReceipts: Int,
    val totalRevenue: Double,
    val currency: String
)

data class AnalyticsChartsDto(
    val userGrowth: List<DataPoint>,
    val receiptTrends: List<DataPoint>,
    val revenueTrends: List<DataPoint>
)

data class DataPoint(
    val timestamp: Long,
    val value: Float
)
