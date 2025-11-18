package com.citrus.admin.data.repository

import com.citrus.admin.data.local.dao.AlertDao
import com.citrus.admin.data.local.entity.AlertEntity
import com.citrus.admin.data.remote.ApiService
import com.citrus.admin.data.remote.dto.toEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface AlertRepository {
    fun getAllActiveAlerts(): Flow<List<AlertEntity>>
    fun getUnreadAlerts(): Flow<List<AlertEntity>>
    fun getUnreadAlertCount(): Flow<Int>
    suspend fun syncAlerts()
    suspend fun markAsRead(alertId: String)
    suspend fun dismissAlert(alertId: String)
}

class AlertRepositoryImpl @Inject constructor(
    private val alertDao: AlertDao,
    private val apiService: ApiService
) : AlertRepository {

    override fun getAllActiveAlerts(): Flow<List<AlertEntity>> =
        alertDao.getAllActiveAlerts()

    override fun getUnreadAlerts(): Flow<List<AlertEntity>> =
        alertDao.getUnreadAlerts()

    override fun getUnreadAlertCount(): Flow<Int> =
        alertDao.getUnreadAlertCount()

    override suspend fun syncAlerts() {
        try {
            val alerts = apiService.getAlerts().map { it.toEntity() }
            alertDao.insertAlerts(alerts)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun markAsRead(alertId: String) {
        alertDao.markAsRead(alertId)
    }

    override suspend fun dismissAlert(alertId: String) {
        try {
            apiService.dismissAlert(alertId)
            alertDao.dismissAlert(alertId)
        } catch (e: Exception) {
            // Dismiss locally and sync later
            alertDao.dismissAlert(alertId)
        }
    }
}
