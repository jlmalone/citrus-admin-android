package com.citrus.admin.data.repository

import com.citrus.admin.data.model.AnalyticsPeriod
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AnalyticsRepositoryTest {

    private lateinit var userRepository: UserRepository
    private lateinit var receiptRepository: ReceiptRepository
    private lateinit var analyticsRepository: AnalyticsRepository

    @Before
    fun setup() {
        userRepository = mockk()
        receiptRepository = mockk()
        analyticsRepository = AnalyticsRepository(userRepository, receiptRepository)
    }

    @Test
    fun `getAnalytics aggregates data correctly`() = runTest {
        coEvery { userRepository.getUserCount() } returns 100
        coEvery { userRepository.getActiveUserCount() } returns 75
        coEvery { receiptRepository.getPendingReceiptCount() } returns 25
        coEvery { receiptRepository.getTotalApprovedAmount() } returns 50000.0

        val analytics = analyticsRepository.getAnalytics(AnalyticsPeriod.MONTH)

        assertThat(analytics.totalUsers).isEqualTo(100)
        assertThat(analytics.activeUsers).isEqualTo(75)
        assertThat(analytics.pendingReceipts).isEqualTo(25)
        assertThat(analytics.totalRevenue).isEqualTo(50000.0)
        assertThat(analytics.period).isEqualTo(AnalyticsPeriod.MONTH)
    }

    @Test
    fun `getAnalytics handles zero values`() = runTest {
        coEvery { userRepository.getUserCount() } returns 0
        coEvery { userRepository.getActiveUserCount() } returns 0
        coEvery { receiptRepository.getPendingReceiptCount() } returns 0
        coEvery { receiptRepository.getTotalApprovedAmount() } returns 0.0

        val analytics = analyticsRepository.getAnalytics()

        assertThat(analytics.totalUsers).isEqualTo(0)
        assertThat(analytics.activeUsers).isEqualTo(0)
        assertThat(analytics.pendingReceipts).isEqualTo(0)
        assertThat(analytics.totalRevenue).isEqualTo(0.0)
    }

    @Test
    fun `getAnalytics uses default period when not specified`() = runTest {
        coEvery { userRepository.getUserCount() } returns 10
        coEvery { userRepository.getActiveUserCount() } returns 5
        coEvery { receiptRepository.getPendingReceiptCount() } returns 2
        coEvery { receiptRepository.getTotalApprovedAmount() } returns 1000.0

        val analytics = analyticsRepository.getAnalytics()

        assertThat(analytics.period).isEqualTo(AnalyticsPeriod.TODAY)
    }

    @Test
    fun `getAnalytics works with different periods`() = runTest {
        coEvery { userRepository.getUserCount() } returns 50
        coEvery { userRepository.getActiveUserCount() } returns 40
        coEvery { receiptRepository.getPendingReceiptCount() } returns 10
        coEvery { receiptRepository.getTotalApprovedAmount() } returns 25000.0

        val periods = listOf(
            AnalyticsPeriod.TODAY,
            AnalyticsPeriod.WEEK,
            AnalyticsPeriod.MONTH,
            AnalyticsPeriod.YEAR
        )

        periods.forEach { period ->
            val analytics = analyticsRepository.getAnalytics(period)
            assertThat(analytics.period).isEqualTo(period)
        }
    }
}
