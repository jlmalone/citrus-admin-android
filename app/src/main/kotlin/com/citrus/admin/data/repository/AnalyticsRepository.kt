package com.citrus.admin.data.repository

import com.citrus.admin.data.model.Analytics
import com.citrus.admin.data.model.AnalyticsPeriod
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsRepository @Inject constructor(
    private val userRepository: UserRepository,
    private val receiptRepository: ReceiptRepository
) {
    suspend fun getAnalytics(period: AnalyticsPeriod = AnalyticsPeriod.TODAY): Analytics {
        val totalUsers = userRepository.getUserCount()
        val activeUsers = userRepository.getActiveUserCount()
        val pendingReceipts = receiptRepository.getPendingReceiptCount()
        val totalRevenue = receiptRepository.getTotalApprovedAmount()

        return Analytics(
            totalUsers = totalUsers,
            activeUsers = activeUsers,
            pendingReceipts = pendingReceipts,
            totalRevenue = totalRevenue,
            period = period
        )
    }
}
