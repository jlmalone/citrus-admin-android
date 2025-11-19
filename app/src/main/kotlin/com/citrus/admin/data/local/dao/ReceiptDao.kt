package com.citrus.admin.data.local.dao

import androidx.room.*
import com.citrus.admin.data.local.entity.ReceiptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceiptDao {
    @Query("SELECT * FROM receipts ORDER BY submittedAt DESC")
    fun getAllReceipts(): Flow<List<ReceiptEntity>>

    @Query("SELECT * FROM receipts WHERE id = :receiptId")
    suspend fun getReceiptById(receiptId: String): ReceiptEntity?

    @Query("SELECT * FROM receipts WHERE status = :status ORDER BY submittedAt DESC")
    fun getReceiptsByStatus(status: String): Flow<List<ReceiptEntity>>

    @Query("SELECT * FROM receipts WHERE userId = :userId ORDER BY submittedAt DESC")
    fun getReceiptsByUser(userId: String): Flow<List<ReceiptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceipt(receipt: ReceiptEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceipts(receipts: List<ReceiptEntity>)

    @Update
    suspend fun updateReceipt(receipt: ReceiptEntity)

    @Delete
    suspend fun deleteReceipt(receipt: ReceiptEntity)

    @Query("SELECT COUNT(*) FROM receipts WHERE status = :status")
    suspend fun getReceiptCountByStatus(status: String): Int

    @Query("SELECT SUM(amount) FROM receipts WHERE status = 'APPROVED'")
    suspend fun getTotalApprovedAmount(): Double?
}
