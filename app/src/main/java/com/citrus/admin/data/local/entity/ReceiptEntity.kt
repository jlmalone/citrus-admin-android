package com.citrus.admin.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "receipts")
data class ReceiptEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val userName: String,
    val amount: Double,
    val currency: String,
    val description: String,
    val category: String,
    val status: String, // "pending", "approved", "rejected"
    val imageUrl: String?,
    val submittedAt: Long,
    val reviewedAt: Long?,
    val reviewedBy: String?,
    val notes: String?,
    val syncedAt: Long = System.currentTimeMillis()
)
