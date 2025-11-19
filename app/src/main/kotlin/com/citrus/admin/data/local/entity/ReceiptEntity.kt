package com.citrus.admin.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.citrus.admin.data.model.Receipt
import com.citrus.admin.data.model.ReceiptStatus

@Entity(tableName = "receipts")
data class ReceiptEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val amount: Double,
    val description: String,
    val status: String,
    val submittedAt: Long,
    val reviewedAt: Long?,
    val reviewerId: String?,
    val category: String
) {
    fun toReceipt(): Receipt = Receipt(
        id = id,
        userId = userId,
        amount = amount,
        description = description,
        status = ReceiptStatus.valueOf(status),
        submittedAt = submittedAt,
        reviewedAt = reviewedAt,
        reviewerId = reviewerId,
        category = category
    )

    companion object {
        fun fromReceipt(receipt: Receipt): ReceiptEntity = ReceiptEntity(
            id = receipt.id,
            userId = receipt.userId,
            amount = receipt.amount,
            description = receipt.description,
            status = receipt.status.name,
            submittedAt = receipt.submittedAt,
            reviewedAt = receipt.reviewedAt,
            reviewerId = receipt.reviewerId,
            category = receipt.category
        )
    }
}
