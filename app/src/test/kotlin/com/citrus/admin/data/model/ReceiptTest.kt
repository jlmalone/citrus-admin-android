package com.citrus.admin.data.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ReceiptTest {

    @Test
    fun `create pending receipt`() {
        val receipt = Receipt(
            id = "receipt123",
            userId = "user123",
            amount = 150.50,
            description = "Office supplies",
            status = ReceiptStatus.PENDING,
            submittedAt = System.currentTimeMillis()
        )

        assertThat(receipt.status).isEqualTo(ReceiptStatus.PENDING)
        assertThat(receipt.reviewedAt).isNull()
        assertThat(receipt.reviewerId).isNull()
        assertThat(receipt.amount).isEqualTo(150.50)
    }

    @Test
    fun `create approved receipt`() {
        val submittedTime = System.currentTimeMillis()
        val reviewedTime = submittedTime + 1000

        val receipt = Receipt(
            id = "receipt456",
            userId = "user456",
            amount = 200.0,
            description = "Travel expenses",
            status = ReceiptStatus.APPROVED,
            submittedAt = submittedTime,
            reviewedAt = reviewedTime,
            reviewerId = "admin123"
        )

        assertThat(receipt.status).isEqualTo(ReceiptStatus.APPROVED)
        assertThat(receipt.reviewedAt).isEqualTo(reviewedTime)
        assertThat(receipt.reviewerId).isEqualTo("admin123")
    }

    @Test
    fun `receipt status changes work correctly`() {
        val receipt = Receipt(
            id = "1",
            userId = "user1",
            amount = 100.0,
            description = "Test",
            status = ReceiptStatus.PENDING,
            submittedAt = System.currentTimeMillis()
        )

        val approved = receipt.copy(
            status = ReceiptStatus.APPROVED,
            reviewedAt = System.currentTimeMillis(),
            reviewerId = "admin1"
        )

        assertThat(receipt.status).isEqualTo(ReceiptStatus.PENDING)
        assertThat(approved.status).isEqualTo(ReceiptStatus.APPROVED)
        assertThat(approved.reviewerId).isNotNull()
    }

    @Test
    fun `receipt amount is positive`() {
        val receipt = Receipt(
            id = "1",
            userId = "user1",
            amount = 50.25,
            description = "Test",
            status = ReceiptStatus.PENDING,
            submittedAt = System.currentTimeMillis()
        )

        assertThat(receipt.amount).isGreaterThan(0.0)
    }

    @Test
    fun `receipt has default category`() {
        val receipt = Receipt(
            id = "1",
            userId = "user1",
            amount = 100.0,
            description = "Test",
            status = ReceiptStatus.PENDING,
            submittedAt = System.currentTimeMillis()
        )

        assertThat(receipt.category).isEqualTo("General")
    }
}
