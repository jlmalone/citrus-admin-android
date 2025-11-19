package com.citrus.admin.data.local.entity

import com.citrus.admin.data.model.Receipt
import com.citrus.admin.data.model.ReceiptStatus
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ReceiptEntityTest {

    @Test
    fun `convert Receipt to ReceiptEntity`() {
        val receipt = Receipt(
            id = "receipt123",
            userId = "user123",
            amount = 150.50,
            description = "Office supplies",
            status = ReceiptStatus.PENDING,
            submittedAt = 1234567890L,
            reviewedAt = null,
            reviewerId = null,
            category = "Office"
        )

        val entity = ReceiptEntity.fromReceipt(receipt)

        assertThat(entity.id).isEqualTo(receipt.id)
        assertThat(entity.userId).isEqualTo(receipt.userId)
        assertThat(entity.amount).isEqualTo(receipt.amount)
        assertThat(entity.description).isEqualTo(receipt.description)
        assertThat(entity.status).isEqualTo(ReceiptStatus.PENDING.name)
        assertThat(entity.submittedAt).isEqualTo(receipt.submittedAt)
        assertThat(entity.reviewedAt).isNull()
        assertThat(entity.reviewerId).isNull()
        assertThat(entity.category).isEqualTo(receipt.category)
    }

    @Test
    fun `convert ReceiptEntity to Receipt`() {
        val entity = ReceiptEntity(
            id = "receipt456",
            userId = "user456",
            amount = 200.0,
            description = "Travel",
            status = ReceiptStatus.APPROVED.name,
            submittedAt = 1000000000L,
            reviewedAt = 1000001000L,
            reviewerId = "admin123",
            category = "Travel"
        )

        val receipt = entity.toReceipt()

        assertThat(receipt.id).isEqualTo(entity.id)
        assertThat(receipt.userId).isEqualTo(entity.userId)
        assertThat(receipt.amount).isEqualTo(entity.amount)
        assertThat(receipt.description).isEqualTo(entity.description)
        assertThat(receipt.status).isEqualTo(ReceiptStatus.APPROVED)
        assertThat(receipt.submittedAt).isEqualTo(entity.submittedAt)
        assertThat(receipt.reviewedAt).isEqualTo(entity.reviewedAt)
        assertThat(receipt.reviewerId).isEqualTo(entity.reviewerId)
        assertThat(receipt.category).isEqualTo(entity.category)
    }

    @Test
    fun `round trip conversion preserves data`() {
        val originalReceipt = Receipt(
            id = "round1",
            userId = "user1",
            amount = 99.99,
            description = "Test",
            status = ReceiptStatus.REJECTED,
            submittedAt = 111111111L,
            reviewedAt = 222222222L,
            reviewerId = "reviewer1",
            category = "Testing"
        )

        val entity = ReceiptEntity.fromReceipt(originalReceipt)
        val convertedReceipt = entity.toReceipt()

        assertThat(convertedReceipt).isEqualTo(originalReceipt)
    }

    @Test
    fun `all receipt statuses can be converted`() {
        ReceiptStatus.values().forEach { status ->
            val receipt = Receipt(
                id = "id",
                userId = "user",
                amount = 100.0,
                description = "desc",
                status = status,
                submittedAt = 123L
            )
            val entity = ReceiptEntity.fromReceipt(receipt)
            val convertedReceipt = entity.toReceipt()

            assertThat(convertedReceipt.status).isEqualTo(status)
        }
    }
}
