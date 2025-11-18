package com.citrus.admin.data.local.dao

import androidx.room.*
import com.citrus.admin.data.local.entity.AlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {

    @Query("SELECT * FROM alerts WHERE isDismissed = 0 ORDER BY timestamp DESC")
    fun getAllActiveAlerts(): Flow<List<AlertEntity>>

    @Query("SELECT * FROM alerts WHERE id = :alertId")
    suspend fun getAlertById(alertId: String): AlertEntity?

    @Query("SELECT * FROM alerts WHERE type = :type AND isDismissed = 0 ORDER BY timestamp DESC")
    fun getAlertsByType(type: String): Flow<List<AlertEntity>>

    @Query("SELECT * FROM alerts WHERE isRead = 0 AND isDismissed = 0")
    fun getUnreadAlerts(): Flow<List<AlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlerts(alerts: List<AlertEntity>)

    @Update
    suspend fun updateAlert(alert: AlertEntity)

    @Delete
    suspend fun deleteAlert(alert: AlertEntity)

    @Query("UPDATE alerts SET isRead = 1 WHERE id = :alertId")
    suspend fun markAsRead(alertId: String)

    @Query("UPDATE alerts SET isDismissed = 1 WHERE id = :alertId")
    suspend fun dismissAlert(alertId: String)

    @Query("DELETE FROM alerts")
    suspend fun deleteAllAlerts()

    @Query("SELECT COUNT(*) FROM alerts WHERE isRead = 0 AND isDismissed = 0")
    fun getUnreadAlertCount(): Flow<Int>
}
