package com.citrus.admin.data.repository

import com.citrus.admin.data.local.dao.ReceiptDao
import com.citrus.admin.data.local.entity.ReceiptEntity
import com.citrus.admin.data.model.Receipt
import com.citrus.admin.data.model.ReceiptStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReceiptRepository @Inject constructor(
    private val receiptDao: ReceiptDao
) {
    fun getAllReceipts(): Flow<List<Receipt>> =
        receiptDao.getAllReceipts().map { entities ->
            entities.map { it.toReceipt() }
        }

    fun getPendingReceipts(): Flow<List<Receipt>> =
        receiptDao.getReceiptsByStatus(ReceiptStatus.PENDING.name).map { entities ->
            entities.map { it.toReceipt() }
        }

    fun getReceiptsByUser(userId: String): Flow<List<Receipt>> =
        receiptDao.getReceiptsByUser(userId).map { entities ->
            entities.map { it.toReceipt() }
        }

    suspend fun getReceiptById(receiptId: String): Receipt? =
        receiptDao.getReceiptById(receiptId)?.toReceipt()

    suspend fun insertReceipt(receipt: Receipt) {
        receiptDao.insertReceipt(ReceiptEntity.fromReceipt(receipt))
    }

    suspend fun insertReceipts(receipts: List<Receipt>) {
        receiptDao.insertReceipts(receipts.map { ReceiptEntity.fromReceipt(it) })
    }

    suspend fun updateReceipt(receipt: Receipt) {
        receiptDao.updateReceipt(ReceiptEntity.fromReceipt(receipt))
    }

    suspend fun approveReceipt(receiptId: String, reviewerId: String) {
        val receipt = receiptDao.getReceiptById(receiptId) ?: return
        val updatedReceipt = receipt.copy(
            status = ReceiptStatus.APPROVED.name,
            reviewerId = reviewerId,
            reviewedAt = System.currentTimeMillis()
        )
        receiptDao.updateReceipt(updatedReceipt)
    }

    suspend fun rejectReceipt(receiptId: String, reviewerId: String) {
        val receipt = receiptDao.getReceiptById(receiptId) ?: return
        val updatedReceipt = receipt.copy(
            status = ReceiptStatus.REJECTED.name,
            reviewerId = reviewerId,
            reviewedAt = System.currentTimeMillis()
        )
        receiptDao.updateReceipt(updatedReceipt)
    }

    suspend fun getPendingReceiptCount(): Int =
        receiptDao.getReceiptCountByStatus(ReceiptStatus.PENDING.name)

    suspend fun getTotalApprovedAmount(): Double =
        receiptDao.getTotalApprovedAmount() ?: 0.0
}
