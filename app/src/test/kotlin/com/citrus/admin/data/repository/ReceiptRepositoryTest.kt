package com.citrus.admin.data.repository

import app.cash.turbine.test
import com.citrus.admin.data.local.dao.ReceiptDao
import com.citrus.admin.data.local.entity.ReceiptEntity
import com.citrus.admin.data.model.Receipt
import com.citrus.admin.data.model.ReceiptStatus
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ReceiptRepositoryTest {

    private lateinit var receiptDao: ReceiptDao
    private lateinit var repository: ReceiptRepository

    @Before
    fun setup() {
        receiptDao = mockk(relaxed = true)
        repository = ReceiptRepository(receiptDao)
    }

    @Test
    fun `getAllReceipts returns mapped receipts`() = runTest {
        val entities = listOf(
            ReceiptEntity("1", "user1", 100.0, "Desc 1", "PENDING", 123L, null, null, "General"),
            ReceiptEntity("2", "user2", 200.0, "Desc 2", "APPROVED", 456L, 789L, "admin1", "Travel")
        )
        coEvery { receiptDao.getAllReceipts() } returns flowOf(entities)

        repository.getAllReceipts().test {
            val receipts = awaitItem()
            assertThat(receipts).hasSize(2)
            assertThat(receipts[0].id).isEqualTo("1")
            assertThat(receipts[1].id).isEqualTo("2")
            awaitComplete()
        }
    }

    @Test
    fun `getPendingReceipts filters by PENDING status`() = runTest {
        val entities = listOf(
            ReceiptEntity("1", "user1", 100.0, "Pending", "PENDING", 123L, null, null, "General")
        )
        coEvery { receiptDao.getReceiptsByStatus("PENDING") } returns flowOf(entities)

        repository.getPendingReceipts().test {
            val receipts = awaitItem()
            assertThat(receipts).hasSize(1)
            assertThat(receipts[0].status).isEqualTo(ReceiptStatus.PENDING)
            awaitComplete()
        }
    }

    @Test
    fun `getReceiptsByUser filters by user id`() = runTest {
        val entities = listOf(
            ReceiptEntity("1", "user123", 100.0, "Test", "PENDING", 123L, null, null, "General")
        )
        coEvery { receiptDao.getReceiptsByUser("user123") } returns flowOf(entities)

        repository.getReceiptsByUser("user123").test {
            val receipts = awaitItem()
            assertThat(receipts).hasSize(1)
            assertThat(receipts[0].userId).isEqualTo("user123")
            awaitComplete()
        }
    }

    @Test
    fun `getReceiptById returns correct receipt`() = runTest {
        val entity = ReceiptEntity("123", "user1", 150.0, "Test", "PENDING", 789L, null, null, "Office")
        coEvery { receiptDao.getReceiptById("123") } returns entity

        val receipt = repository.getReceiptById("123")

        assertThat(receipt).isNotNull()
        assertThat(receipt?.id).isEqualTo("123")
        assertThat(receipt?.amount).isEqualTo(150.0)
    }

    @Test
    fun `insertReceipt calls dao`() = runTest {
        val receipt = Receipt("1", "user1", 100.0, "Test", ReceiptStatus.PENDING, 123L)

        repository.insertReceipt(receipt)

        coVerify { receiptDao.insertReceipt(any()) }
    }

    @Test
    fun `approveReceipt updates status and adds reviewer info`() = runTest {
        val originalEntity = ReceiptEntity("1", "user1", 100.0, "Test", "PENDING", 123L, null, null, "General")
        coEvery { receiptDao.getReceiptById("1") } returns originalEntity

        val slot = slot<ReceiptEntity>()
        coEvery { receiptDao.updateReceipt(capture(slot)) } returns Unit

        repository.approveReceipt("1", "admin123")

        coVerify { receiptDao.updateReceipt(any()) }
        assertThat(slot.captured.status).isEqualTo("APPROVED")
        assertThat(slot.captured.reviewerId).isEqualTo("admin123")
        assertThat(slot.captured.reviewedAt).isNotNull()
    }

    @Test
    fun `rejectReceipt updates status and adds reviewer info`() = runTest {
        val originalEntity = ReceiptEntity("1", "user1", 100.0, "Test", "PENDING", 123L, null, null, "General")
        coEvery { receiptDao.getReceiptById("1") } returns originalEntity

        val slot = slot<ReceiptEntity>()
        coEvery { receiptDao.updateReceipt(capture(slot)) } returns Unit

        repository.rejectReceipt("1", "admin456")

        coVerify { receiptDao.updateReceipt(any()) }
        assertThat(slot.captured.status).isEqualTo("REJECTED")
        assertThat(slot.captured.reviewerId).isEqualTo("admin456")
        assertThat(slot.captured.reviewedAt).isNotNull()
    }

    @Test
    fun `approveReceipt does nothing when receipt not found`() = runTest {
        coEvery { receiptDao.getReceiptById("nonexistent") } returns null

        repository.approveReceipt("nonexistent", "admin123")

        coVerify(exactly = 0) { receiptDao.updateReceipt(any()) }
    }

    @Test
    fun `getPendingReceiptCount returns correct count`() = runTest {
        coEvery { receiptDao.getReceiptCountByStatus("PENDING") } returns 15

        val count = repository.getPendingReceiptCount()

        assertThat(count).isEqualTo(15)
    }

    @Test
    fun `getTotalApprovedAmount returns sum`() = runTest {
        coEvery { receiptDao.getTotalApprovedAmount() } returns 5000.50

        val total = repository.getTotalApprovedAmount()

        assertThat(total).isEqualTo(5000.50)
    }

    @Test
    fun `getTotalApprovedAmount returns 0 when null`() = runTest {
        coEvery { receiptDao.getTotalApprovedAmount() } returns null

        val total = repository.getTotalApprovedAmount()

        assertThat(total).isEqualTo(0.0)
    }
}
