package com.citrus.admin.ui.receipts

import app.cash.turbine.test
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.citrus.admin.data.local.entity.ReceiptEntity
import com.citrus.admin.data.repository.ReceiptRepository
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
class ReceiptViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: ReceiptRepository
    private lateinit var viewModel: ReceiptViewModel
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
    fun `initial state shows pending receipts`() = runTest {
        coEvery { repository.getReceiptsByStatus("pending") } returns flowOf(emptyList())
        coEvery { repository.syncReceipts() } returns Unit

        viewModel = ReceiptViewModel(repository)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.selectedFilter).isEqualTo(ReceiptFilter.PENDING)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getPendingReceipts returns pending list`() = runTest {
        val receipts = listOf(
            ReceiptEntity("1", "user1", 100.0, "Desc", "pending", 123L, null, null),
            ReceiptEntity("2", "user2", 200.0, "Desc 2", "pending", 456L, null, null)
        )
        coEvery { repository.getReceiptsByStatus("pending") } returns flowOf(receipts)
        coEvery { repository.syncReceipts() } returns Unit

        viewModel = ReceiptViewModel(repository)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.receipts).hasSize(2)
            assertThat(state.receipts[0].status).isEqualTo("pending")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onFilterChanged updates filter and queries correct status`() = runTest {
        coEvery { repository.getReceiptsByStatus(any()) } returns flowOf(emptyList())
        coEvery { repository.syncReceipts() } returns Unit

        viewModel = ReceiptViewModel(repository)
        advanceUntilIdle()

        viewModel.onFilterChanged(ReceiptFilter.APPROVED)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.selectedFilter).isEqualTo(ReceiptFilter.APPROVED)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { repository.getReceiptsByStatus("approved") }
    }

    @Test
    fun `syncReceipts calls repository sync`() = runTest {
        coEvery { repository.getReceiptsByStatus(any()) } returns flowOf(emptyList())
        coEvery { repository.syncReceipts() } returns Unit

        viewModel = ReceiptViewModel(repository)
        advanceUntilIdle()

        viewModel.syncReceipts()
        advanceUntilIdle()

        coVerify(atLeast = 2) { repository.syncReceipts() }
    }

    @Test
    fun `syncReceipts handles errors`() = runTest {
        coEvery { repository.getReceiptsByStatus(any()) } returns flowOf(emptyList())
        coEvery { repository.syncReceipts() } throws Exception("Sync failed")

        viewModel = ReceiptViewModel(repository)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("Sync failed")
            assertThat(state.isLoading).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `approveReceipt calls repository with correct params`() = runTest {
        coEvery { repository.getReceiptsByStatus(any()) } returns flowOf(emptyList())
        coEvery { repository.syncReceipts() } returns Unit
        coEvery { repository.reviewReceipt(any(), any(), any()) } returns Unit

        viewModel = ReceiptViewModel(repository)
        advanceUntilIdle()

        viewModel.approveReceipt("receipt123", "Looks good")
        advanceUntilIdle()

        coVerify { repository.reviewReceipt("receipt123", "approved", "Looks good") }
    }

    @Test
    fun `rejectReceipt calls repository with correct params`() = runTest {
        coEvery { repository.getReceiptsByStatus(any()) } returns flowOf(emptyList())
        coEvery { repository.syncReceipts() } returns Unit
        coEvery { repository.reviewReceipt(any(), any(), any()) } returns Unit

        viewModel = ReceiptViewModel(repository)
        advanceUntilIdle()

        viewModel.rejectReceipt("receipt456", "Invalid expense")
        advanceUntilIdle()

        coVerify { repository.reviewReceipt("receipt456", "rejected", "Invalid expense") }
    }

    @Test
    fun `approveReceipt handles errors`() = runTest {
        coEvery { repository.getReceiptsByStatus(any()) } returns flowOf(emptyList())
        coEvery { repository.syncReceipts() } returns Unit
        coEvery { repository.reviewReceipt(any(), any(), any()) } throws Exception("Review failed")

        viewModel = ReceiptViewModel(repository)
        advanceUntilIdle()

        viewModel.approveReceipt("receipt123")
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("Review failed")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearError resets error state`() = runTest {
        coEvery { repository.getReceiptsByStatus(any()) } returns flowOf(emptyList())
        coEvery { repository.syncReceipts() } throws Exception("Error")

        viewModel = ReceiptViewModel(repository)
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
    fun `filter ALL queries all receipts`() = runTest {
        coEvery { repository.getReceiptsByStatus(any()) } returns flowOf(emptyList())
        coEvery { repository.getAllReceipts() } returns flowOf(emptyList())
        coEvery { repository.syncReceipts() } returns Unit

        viewModel = ReceiptViewModel(repository)
        advanceUntilIdle()

        viewModel.onFilterChanged(ReceiptFilter.ALL)
        advanceUntilIdle()

        coVerify { repository.getAllReceipts() }
    }

    @Test
    fun `filter REJECTED queries rejected receipts`() = runTest {
        coEvery { repository.getReceiptsByStatus(any()) } returns flowOf(emptyList())
        coEvery { repository.syncReceipts() } returns Unit

        viewModel = ReceiptViewModel(repository)
        advanceUntilIdle()

        viewModel.onFilterChanged(ReceiptFilter.REJECTED)
        advanceUntilIdle()

        coVerify { repository.getReceiptsByStatus("rejected") }
    }
}
