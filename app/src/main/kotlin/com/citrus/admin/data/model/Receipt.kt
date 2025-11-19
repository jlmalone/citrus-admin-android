package com.citrus.admin.data.model

data class Receipt(
    val id: String,
    val userId: String,
    val amount: Double,
    val description: String,
    val status: ReceiptStatus,
    val submittedAt: Long,
    val reviewedAt: Long? = null,
    val reviewerId: String? = null,
    val category: String = "General"
)

enum class ReceiptStatus {
    PENDING,
    APPROVED,
    REJECTED
}
