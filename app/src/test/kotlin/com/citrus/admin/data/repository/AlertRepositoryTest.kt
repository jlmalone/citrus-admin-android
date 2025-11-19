package com.citrus.admin.data.repository

import app.cash.turbine.test
import com.citrus.admin.data.local.dao.AlertDao
import com.citrus.admin.data.local.entity.AlertEntity
import com.citrus.admin.data.remote.ApiService
import com.citrus.admin.data.remote.dto.AlertDto
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AlertRepositoryTest {

    private lateinit var alertDao: AlertDao
    private lateinit var apiService: ApiService
    private lateinit var repository: AlertRepository

    @Before
    fun setup() {
        alertDao = mockk(relaxed = true)
        apiService = mockk(relaxed = true)
        repository = AlertRepositoryImpl(alertDao, apiService)
    }

    @Test
    fun `getAllActiveAlerts returns alerts from dao`() = runTest {
        val alerts = listOf(
            AlertEntity("1", "Alert 1", "high", "Message 1", 123L, false, false),
            AlertEntity("2", "Alert 2", "medium", "Message 2", 456L, false, false)
        )
        coEvery { alertDao.getAllActiveAlerts() } returns flowOf(alerts)

        repository.getAllActiveAlerts().test {
            val result = awaitItem()
            assertThat(result).hasSize(2)
            assertThat(result[0].title).isEqualTo("Alert 1")
            awaitComplete()
        }
    }

    @Test
    fun `getUnreadAlerts returns unread alerts`() = runTest {
        val alerts = listOf(
            AlertEntity("1", "Unread Alert", "high", "Message", 123L, false, false)
        )
        coEvery { alertDao.getUnreadAlerts() } returns flowOf(alerts)

        repository.getUnreadAlerts().test {
            val result = awaitItem()
            assertThat(result).hasSize(1)
            assertThat(result[0].isRead).isFalse()
            awaitComplete()
        }
    }

    @Test
    fun `getUnreadAlertCount returns count`() = runTest {
        coEvery { alertDao.getUnreadAlertCount() } returns flowOf(5)

        repository.getUnreadAlertCount().test {
            val count = awaitItem()
            assertThat(count).isEqualTo(5)
            awaitComplete()
        }
    }

    @Test
    fun `syncAlerts fetches from API and inserts to dao`() = runTest {
        val apiAlerts = listOf(
            AlertDto("1", "Alert 1", "high", "Message 1", 123L)
        )
        coEvery { apiService.getAlerts() } returns apiAlerts
        coEvery { alertDao.insertAlerts(any()) } returns Unit

        repository.syncAlerts()

        coVerify { apiService.getAlerts() }
        coVerify { alertDao.insertAlerts(any()) }
    }

    @Test
    fun `syncAlerts handles API errors gracefully`() = runTest {
        coEvery { apiService.getAlerts() } throws Exception("Network error")

        // Should not throw exception
        repository.syncAlerts()

        coVerify { apiService.getAlerts() }
        coVerify(exactly = 0) { alertDao.insertAlerts(any()) }
    }

    @Test
    fun `markAsRead calls dao`() = runTest {
        coEvery { alertDao.markAsRead("alert123") } returns Unit

        repository.markAsRead("alert123")

        coVerify { alertDao.markAsRead("alert123") }
    }

    @Test
    fun `dismissAlert calls API and dao`() = runTest {
        coEvery { apiService.dismissAlert("alert123") } returns Unit
        coEvery { alertDao.dismissAlert("alert123") } returns Unit

        repository.dismissAlert("alert123")

        coVerify { apiService.dismissAlert("alert123") }
        coVerify { alertDao.dismissAlert("alert123") }
    }

    @Test
    fun `dismissAlert dismisses locally when API fails`() = runTest {
        coEvery { apiService.dismissAlert("alert123") } throws Exception("API error")
        coEvery { alertDao.dismissAlert("alert123") } returns Unit

        repository.dismissAlert("alert123")

        coVerify { apiService.dismissAlert("alert123") }
        coVerify { alertDao.dismissAlert("alert123") }
    }
}
