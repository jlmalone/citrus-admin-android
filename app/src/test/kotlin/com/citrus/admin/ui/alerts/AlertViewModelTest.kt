package com.citrus.admin.ui.alerts

import app.cash.turbine.test
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.citrus.admin.data.local.entity.AlertEntity
import com.citrus.admin.data.repository.AlertRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AlertViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: AlertRepository
    private lateinit var viewModel: AlertViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        coEvery { repository.getAllActiveAlerts() } returns flowOf(emptyList())
        coEvery { repository.getUnreadAlertCount() } returns flowOf(0)
        coEvery { repository.syncAlerts() } returns Unit

        viewModel = AlertViewModel(repository)

        viewModel.uiState.test {
            val initialState = awaitItem()
            assertThat(initialState.isLoading).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getAllActiveAlerts returns alert list`() = runTest {
        val alerts = listOf(
            AlertEntity("1", "Alert 1", "high", "Message 1", 123L, false, false),
            AlertEntity("2", "Alert 2", "medium", "Message 2", 456L, false, false)
        )
        coEvery { repository.getAllActiveAlerts() } returns flowOf(alerts)
        coEvery { repository.getUnreadAlertCount() } returns flowOf(2)
        coEvery { repository.syncAlerts() } returns Unit

        viewModel = AlertViewModel(repository)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.alerts).hasSize(2)
            assertThat(state.alerts[0].title).isEqualTo("Alert 1")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `unread count is displayed correctly`() = runTest {
        coEvery { repository.getAllActiveAlerts() } returns flowOf(emptyList())
        coEvery { repository.getUnreadAlertCount() } returns flowOf(5)
        coEvery { repository.syncAlerts() } returns Unit

        viewModel = AlertViewModel(repository)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.unreadCount).isEqualTo(5)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `syncAlerts calls repository`() = runTest {
        coEvery { repository.getAllActiveAlerts() } returns flowOf(emptyList())
        coEvery { repository.getUnreadAlertCount() } returns flowOf(0)
        coEvery { repository.syncAlerts() } returns Unit

        viewModel = AlertViewModel(repository)
        advanceUntilIdle()

        viewModel.syncAlerts()
        advanceUntilIdle()

        coVerify(atLeast = 2) { repository.syncAlerts() }
    }

    @Test
    fun `syncAlerts handles errors`() = runTest {
        coEvery { repository.getAllActiveAlerts() } returns flowOf(emptyList())
        coEvery { repository.getUnreadAlertCount() } returns flowOf(0)
        coEvery { repository.syncAlerts() } throws Exception("Sync error")

        viewModel = AlertViewModel(repository)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("Sync error")
            assertThat(state.isLoading).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `markAsRead calls repository`() = runTest {
        coEvery { repository.getAllActiveAlerts() } returns flowOf(emptyList())
        coEvery { repository.getUnreadAlertCount() } returns flowOf(0)
        coEvery { repository.syncAlerts() } returns Unit
        coEvery { repository.markAsRead(any()) } returns Unit

        viewModel = AlertViewModel(repository)
        advanceUntilIdle()

        viewModel.markAsRead("alert123")
        advanceUntilIdle()

        coVerify { repository.markAsRead("alert123") }
    }

    @Test
    fun `markAsRead handles errors`() = runTest {
        coEvery { repository.getAllActiveAlerts() } returns flowOf(emptyList())
        coEvery { repository.getUnreadAlertCount() } returns flowOf(0)
        coEvery { repository.syncAlerts() } returns Unit
        coEvery { repository.markAsRead(any()) } throws Exception("Mark failed")

        viewModel = AlertViewModel(repository)
        advanceUntilIdle()

        viewModel.markAsRead("alert123")
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("Mark failed")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `dismissAlert calls repository`() = runTest {
        coEvery { repository.getAllActiveAlerts() } returns flowOf(emptyList())
        coEvery { repository.getUnreadAlertCount() } returns flowOf(0)
        coEvery { repository.syncAlerts() } returns Unit
        coEvery { repository.dismissAlert(any()) } returns Unit

        viewModel = AlertViewModel(repository)
        advanceUntilIdle()

        viewModel.dismissAlert("alert456")
        advanceUntilIdle()

        coVerify { repository.dismissAlert("alert456") }
    }

    @Test
    fun `dismissAlert handles errors`() = runTest {
        coEvery { repository.getAllActiveAlerts() } returns flowOf(emptyList())
        coEvery { repository.getUnreadAlertCount() } returns flowOf(0)
        coEvery { repository.syncAlerts() } returns Unit
        coEvery { repository.dismissAlert(any()) } throws Exception("Dismiss failed")

        viewModel = AlertViewModel(repository)
        advanceUntilIdle()

        viewModel.dismissAlert("alert456")
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("Dismiss failed")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearError resets error state`() = runTest {
        coEvery { repository.getAllActiveAlerts() } returns flowOf(emptyList())
        coEvery { repository.getUnreadAlertCount() } returns flowOf(0)
        coEvery { repository.syncAlerts() } throws Exception("Error")

        viewModel = AlertViewModel(repository)
        advanceUntilIdle()

        viewModel.clearError()
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
