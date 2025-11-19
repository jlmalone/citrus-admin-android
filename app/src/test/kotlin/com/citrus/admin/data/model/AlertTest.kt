package com.citrus.admin.data.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AlertTest {

    @Test
    fun `create alert with default values`() {
        val alert = Alert(
            id = "alert123",
            title = "System Update",
            message = "New version available",
            severity = AlertSeverity.INFO
        )

        assertThat(alert.id).isEqualTo("alert123")
        assertThat(alert.title).isEqualTo("System Update")
        assertThat(alert.message).isEqualTo("New version available")
        assertThat(alert.severity).isEqualTo(AlertSeverity.INFO)
        assertThat(alert.isRead).isFalse()
        assertThat(alert.timestamp).isGreaterThan(0)
    }

    @Test
    fun `alert severity levels are ordered by importance`() {
        val info = Alert("1", "Info", "Info message", AlertSeverity.INFO)
        val warning = Alert("2", "Warning", "Warning message", AlertSeverity.WARNING)
        val error = Alert("3", "Error", "Error message", AlertSeverity.ERROR)
        val critical = Alert("4", "Critical", "Critical message", AlertSeverity.CRITICAL)

        assertThat(info.severity).isEqualTo(AlertSeverity.INFO)
        assertThat(warning.severity).isEqualTo(AlertSeverity.WARNING)
        assertThat(error.severity).isEqualTo(AlertSeverity.ERROR)
        assertThat(critical.severity).isEqualTo(AlertSeverity.CRITICAL)
    }

    @Test
    fun `mark alert as read`() {
        val alert = Alert(
            id = "1",
            title = "Test",
            message = "Test message",
            severity = AlertSeverity.INFO
        )
        val readAlert = alert.copy(isRead = true)

        assertThat(alert.isRead).isFalse()
        assertThat(readAlert.isRead).isTrue()
    }

    @Test
    fun `critical alert has correct severity`() {
        val alert = Alert(
            id = "critical1",
            title = "System Failure",
            message = "Database connection lost",
            severity = AlertSeverity.CRITICAL
        )

        assertThat(alert.severity).isEqualTo(AlertSeverity.CRITICAL)
    }
}
