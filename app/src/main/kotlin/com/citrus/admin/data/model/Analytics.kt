package com.citrus.admin.data.model

data class Analytics(
    val totalUsers: Int,
    val activeUsers: Int,
    val pendingReceipts: Int,
    val totalRevenue: Double,
    val period: AnalyticsPeriod
)

enum class AnalyticsPeriod {
    TODAY,
    WEEK,
    MONTH,
    YEAR
}
