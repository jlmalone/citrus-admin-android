package com.citrus.admin.data.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AnalyticsTest {

    @Test
    fun `create analytics with valid data`() {
        val analytics = Analytics(
            totalUsers = 100,
            activeUsers = 75,
            pendingReceipts = 25,
            totalRevenue = 50000.0,
            period = AnalyticsPeriod.MONTH
        )

        assertThat(analytics.totalUsers).isEqualTo(100)
        assertThat(analytics.activeUsers).isEqualTo(75)
        assertThat(analytics.pendingReceipts).isEqualTo(25)
        assertThat(analytics.totalRevenue).isEqualTo(50000.0)
        assertThat(analytics.period).isEqualTo(AnalyticsPeriod.MONTH)
    }

    @Test
    fun `active users cannot exceed total users`() {
        val analytics = Analytics(
            totalUsers = 100,
            activeUsers = 75,
            pendingReceipts = 10,
            totalRevenue = 1000.0,
            period = AnalyticsPeriod.TODAY
        )

        assertThat(analytics.activeUsers).isAtMost(analytics.totalUsers)
    }

    @Test
    fun `analytics period types are distinct`() {
        val periods = listOf(
            AnalyticsPeriod.TODAY,
            AnalyticsPeriod.WEEK,
            AnalyticsPeriod.MONTH,
            AnalyticsPeriod.YEAR
        )

        assertThat(periods).hasSize(4)
        assertThat(periods.distinct()).hasSize(4)
    }

    @Test
    fun `zero values are valid`() {
        val analytics = Analytics(
            totalUsers = 0,
            activeUsers = 0,
            pendingReceipts = 0,
            totalRevenue = 0.0,
            period = AnalyticsPeriod.TODAY
        )

        assertThat(analytics.totalUsers).isEqualTo(0)
        assertThat(analytics.activeUsers).isEqualTo(0)
        assertThat(analytics.pendingReceipts).isEqualTo(0)
        assertThat(analytics.totalRevenue).isEqualTo(0.0)
    }
}
