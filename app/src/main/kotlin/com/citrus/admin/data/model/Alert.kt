package com.citrus.admin.data.model

data class Alert(
    val id: String,
    val title: String,
    val message: String,
    val severity: AlertSeverity,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

enum class AlertSeverity {
    INFO,
    WARNING,
    ERROR,
    CRITICAL
}
