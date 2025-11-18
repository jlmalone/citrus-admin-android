package com.citrus.admin.data.repository

import com.citrus.admin.data.local.dao.ReceiptDao
import com.citrus.admin.data.local.entity.ReceiptEntity
import com.citrus.admin.data.remote.ApiService
import com.citrus.admin.data.remote.ReviewReceiptRequest
import com.citrus.admin.data.remote.dto.toEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ReceiptRepository {
    fun getAllReceipts(): Flow<List<ReceiptEntity>>
    fun getReceiptsByStatus(status: String): Flow<List<ReceiptEntity>>
    fun getReceiptCount(): Flow<Int>
    fun getPendingReceiptCount(): Flow<Int>
    fun getTotalApprovedAmount(): Flow<Double?>
    suspend fun syncReceipts()
    suspend fun reviewReceipt(receiptId: String, status: String, notes: String?)
}

class ReceiptRepositoryImpl @Inject constructor(
    private val receiptDao: ReceiptDao,
    private val apiService: ApiService
) : ReceiptRepository {

    override fun getAllReceipts(): Flow<List<ReceiptEntity>> =
        receiptDao.getAllReceipts()

    override fun getReceiptsByStatus(status: String): Flow<List<ReceiptEntity>> =
        receiptDao.getReceiptsByStatus(status)

    override fun getReceiptCount(): Flow<Int> =
        receiptDao.getReceiptCount()

    override fun getPendingReceiptCount(): Flow<Int> =
        receiptDao.getPendingReceiptCount()

    override fun getTotalApprovedAmount(): Flow<Double?> =
        receiptDao.getTotalApprovedAmount()

    override suspend fun syncReceipts() {
        try {
            val receipts = apiService.getReceipts().map { it.toEntity() }
            receiptDao.insertReceipts(receipts)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun reviewReceipt(receiptId: String, status: String, notes: String?) {
        try {
            val updated = apiService.reviewReceipt(
                receiptId,
                ReviewReceiptRequest(status, notes)
            )
            receiptDao.updateReceipt(updated.toEntity())
        } catch (e: Exception) {
            // Update locally and sync later
            val receipt = receiptDao.getReceiptById(receiptId)
            receipt?.let {
                receiptDao.updateReceipt(
                    it.copy(
                        status = status,
                        notes = notes,
                        reviewedAt = System.currentTimeMillis()
                    )
                )
            }
        }
    }
}
