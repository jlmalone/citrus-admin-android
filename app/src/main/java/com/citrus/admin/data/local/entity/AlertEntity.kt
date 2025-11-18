package com.citrus.admin.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alerts")
data class AlertEntity(
    @PrimaryKey
    val id: String,
    val type: String, // "info", "warning", "error"
    val title: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean = false,
    val isDismissed: Boolean = false,
    val actionUrl: String?,
    val syncedAt: Long = System.currentTimeMillis()
)
