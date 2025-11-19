package com.citrus.admin.ui.analytics

import app.cash.turbine.test
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.citrus.admin.data.remote.dto.AnalyticsChartsDto
import com.citrus.admin.data.remote.dto.AnalyticsSummaryDto
import com.citrus.admin.data.repository.AnalyticsRepository
import com.citrus.admin.data.repository.ReceiptRepository
import com.citrus.admin.data.repository.UserRepository
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
class AnalyticsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var analyticsRepository: AnalyticsRepository
    private lateinit var userRepository: UserRepository
    private lateinit var receiptRepository: ReceiptRepository
    private lateinit var viewModel: AnalyticsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        analyticsRepository = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)
        receiptRepository = mockk(relaxed = true)

        // Setup default flows
        coEvery { userRepository.getUserCount() } returns flowOf(0)
        coEvery { userRepository.getActiveUserCount() } returns flowOf(0)
        coEvery { receiptRepository.getReceiptCount() } returns flowOf(0)
        coEvery { receiptRepository.getPendingReceiptCount() } returns flowOf(0)
        coEvery { receiptRepository.getTotalApprovedAmount() } returns flowOf(0.0)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        coEvery { analyticsRepository.getAnalyticsSummary() } returns Result.success(mockk())
        coEvery { analyticsRepository.getAnalyticsCharts(any()) } returns Result.success(mockk())

        viewModel = AnalyticsViewModel(analyticsRepository, userRepository, receiptRepository)

        viewModel.uiState.test {
            val initialState = awaitItem()
            assertThat(initialState.isLoading).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadAnalytics fetches summary and charts`() = runTest {
        val summary = mockk<AnalyticsSummaryDto>(relaxed = true)
        val charts = mockk<AnalyticsChartsDto>(relaxed = true)

        coEvery { analyticsRepository.getAnalyticsSummary() } returns Result.success(summary)
        coEvery { analyticsRepository.getAnalyticsCharts(any()) } returns Result.success(charts)

        viewModel = AnalyticsViewModel(analyticsRepository, userRepository, receiptRepository)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.summary).isEqualTo(summary)
            assertThat(state.charts).isEqualTo(charts)
            assertThat(state.isLoading).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadAnalytics handles summary fetch error`() = runTest {
        coEvery { analyticsRepository.getAnalyticsSummary() } returns Result.failure(Exception("Summary error"))
        coEvery { analyticsRepository.getAnalyticsCharts(any()) } returns Result.success(mockk())

        viewModel = AnalyticsViewModel(analyticsRepository, userRepository, receiptRepository)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("Summary error")
            assertThat(state.isLoading).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadAnalytics handles charts fetch error`() = runTest {
        coEvery { analyticsRepository.getAnalyticsSummary() } returns Result.success(mockk())
        coEvery { analyticsRepository.getAnalyticsCharts(any()) } returns Result.failure(Exception("Charts error"))

        viewModel = AnalyticsViewModel(analyticsRepository, userRepository, receiptRepository)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("Charts error")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onPeriodChanged updates period and refetches charts`() = runTest {
        val charts = mockk<AnalyticsChartsDto>(relaxed = true)
        coEvery { analyticsRepository.getAnalyticsSummary() } returns Result.success(mockk())
        coEvery { analyticsRepository.getAnalyticsCharts(any()) } returns Result.success(charts)

        viewModel = AnalyticsViewModel(analyticsRepository, userRepository, receiptRepository)
        advanceUntilIdle()

        viewModel.onPeriodChanged("month")
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.selectedPeriod).isEqualTo("month")
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { analyticsRepository.getAnalyticsCharts("month") }
    }

    @Test
    fun `onPeriodChanged handles error`() = runTest {
        coEvery { analyticsRepository.getAnalyticsSummary() } returns Result.success(mockk())
        coEvery { analyticsRepository.getAnalyticsCharts("week") } returns Result.success(mockk())
        coEvery { analyticsRepository.getAnalyticsCharts("year") } returns Result.failure(Exception("Period error"))

        viewModel = AnalyticsViewModel(analyticsRepository, userRepository, receiptRepository)
        advanceUntilIdle()

        viewModel.onPeriodChanged("year")
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("Period error")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearError resets error state`() = runTest {
        coEvery { analyticsRepository.getAnalyticsSummary() } returns Result.failure(Exception("Error"))
        coEvery { analyticsRepository.getAnalyticsCharts(any()) } returns Result.success(mockk())

        viewModel = AnalyticsViewModel(analyticsRepository, userRepository, receiptRepository)
        advanceUntilIdle()

        viewModel.clearError()
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `local stats are exposed as state flows`() = runTest {
        coEvery { userRepository.getUserCount() } returns flowOf(100)
        coEvery { userRepository.getActiveUserCount() } returns flowOf(75)
        coEvery { receiptRepository.getReceiptCount() } returns flowOf(500)
        coEvery { receiptRepository.getPendingReceiptCount() } returns flowOf(25)
        coEvery { receiptRepository.getTotalApprovedAmount() } returns flowOf(50000.0)
        coEvery { analyticsRepository.getAnalyticsSummary() } returns Result.success(mockk())
        coEvery { analyticsRepository.getAnalyticsCharts(any()) } returns Result.success(mockk())

        viewModel = AnalyticsViewModel(analyticsRepository, userRepository, receiptRepository)
        advanceUntilIdle()

        viewModel.userCount.test {
            assertThat(awaitItem()).isEqualTo(100)
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.activeUserCount.test {
            assertThat(awaitItem()).isEqualTo(75)
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.totalRevenue.test {
            assertThat(awaitItem()).isEqualTo(50000.0)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `default period is week`() = runTest {
        coEvery { analyticsRepository.getAnalyticsSummary() } returns Result.success(mockk())
        coEvery { analyticsRepository.getAnalyticsCharts(any()) } returns Result.success(mockk())

        viewModel = AnalyticsViewModel(analyticsRepository, userRepository, receiptRepository)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.selectedPeriod).isEqualTo("week")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadAnalytics can be called manually`() = runTest {
        coEvery { analyticsRepository.getAnalyticsSummary() } returns Result.success(mockk())
        coEvery { analyticsRepository.getAnalyticsCharts(any()) } returns Result.success(mockk())

        viewModel = AnalyticsViewModel(analyticsRepository, userRepository, receiptRepository)
        advanceUntilIdle()

        viewModel.loadAnalytics()
        advanceUntilIdle()

        coVerify(atLeast = 2) { analyticsRepository.getAnalyticsSummary() }
        coVerify(atLeast = 2) { analyticsRepository.getAnalyticsCharts(any()) }
    }
}
