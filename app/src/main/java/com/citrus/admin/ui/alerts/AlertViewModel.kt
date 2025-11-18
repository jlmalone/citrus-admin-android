package com.citrus.admin.ui.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citrus.admin.data.local.entity.AlertEntity
import com.citrus.admin.data.repository.AlertRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AlertUiState(
    val alerts: List<AlertEntity> = emptyList(),
    val unreadCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AlertViewModel @Inject constructor(
    private val alertRepository: AlertRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<AlertUiState> = combine(
        alertRepository.getAllActiveAlerts(),
        alertRepository.getUnreadAlertCount(),
        _isLoading,
        _error
    ) { alerts, unreadCount, isLoading, error ->
        AlertUiState(
            alerts = alerts,
            unreadCount = unreadCount,
            isLoading = isLoading,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AlertUiState(isLoading = true)
    )

    init {
        syncAlerts()
    }

    fun syncAlerts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                alertRepository.syncAlerts()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun markAsRead(alertId: String) {
        viewModelScope.launch {
            try {
                alertRepository.markAsRead(alertId)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun dismissAlert(alertId: String) {
        viewModelScope.launch {
            try {
                alertRepository.dismissAlert(alertId)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
