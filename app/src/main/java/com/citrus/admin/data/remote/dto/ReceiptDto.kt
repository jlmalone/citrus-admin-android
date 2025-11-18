package com.citrus.admin.data.remote.dto

import com.citrus.admin.data.local.entity.ReceiptEntity

data class ReceiptDto(
    val id: String,
    val userId: String,
    val userName: String,
    val amount: Double,
    val currency: String,
    val description: String,
    val category: String,
    val status: String,
    val imageUrl: String?,
    val submittedAt: Long,
    val reviewedAt: Long?,
    val reviewedBy: String?,
    val notes: String?
)

fun ReceiptDto.toEntity() = ReceiptEntity(
    id = id,
    userId = userId,
    userName = userName,
    amount = amount,
    currency = currency,
    description = description,
    category = category,
    status = status,
    imageUrl = imageUrl,
    submittedAt = submittedAt,
    reviewedAt = reviewedAt,
    reviewedBy = reviewedBy,
    notes = notes
)
