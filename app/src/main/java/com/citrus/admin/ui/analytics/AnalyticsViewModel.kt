package com.citrus.admin.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citrus.admin.data.remote.dto.AnalyticsChartsDto
import com.citrus.admin.data.remote.dto.AnalyticsSummaryDto
import com.citrus.admin.data.repository.AnalyticsRepository
import com.citrus.admin.data.repository.ReceiptRepository
import com.citrus.admin.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AnalyticsUiState(
    val summary: AnalyticsSummaryDto? = null,
    val charts: AnalyticsChartsDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedPeriod: String = "week"
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val analyticsRepository: AnalyticsRepository,
    private val userRepository: UserRepository,
    private val receiptRepository: ReceiptRepository
) : ViewModel() {

    private val _summary = MutableStateFlow<AnalyticsSummaryDto?>(null)
    private val _charts = MutableStateFlow<AnalyticsChartsDto?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    private val _selectedPeriod = MutableStateFlow("week")

    val uiState: StateFlow<AnalyticsUiState> = combine(
        _summary,
        _charts,
        _isLoading,
        _error,
        _selectedPeriod
    ) { summary, charts, isLoading, error, period ->
        AnalyticsUiState(
            summary = summary,
            charts = charts,
            isLoading = isLoading,
            error = error,
            selectedPeriod = period
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnalyticsUiState(isLoading = true)
    )

    // Local stats for offline mode
    val userCount = userRepository.getUserCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val activeUserCount = userRepository.getActiveUserCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val receiptCount = receiptRepository.getReceiptCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val pendingReceiptCount = receiptRepository.getPendingReceiptCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalRevenue = receiptRepository.getTotalApprovedAmount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    init {
        loadAnalytics()
    }

    fun loadAnalytics() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            // Load summary
            analyticsRepository.getAnalyticsSummary()
                .onSuccess { _summary.value = it }
                .onFailure { _error.value = it.message }

            // Load charts
            analyticsRepository.getAnalyticsCharts(_selectedPeriod.value)
                .onSuccess { _charts.value = it }
                .onFailure { _error.value = it.message }

            _isLoading.value = false
        }
    }

    fun onPeriodChanged(period: String) {
        _selectedPeriod.value = period
        viewModelScope.launch {
            analyticsRepository.getAnalyticsCharts(period)
                .onSuccess { _charts.value = it }
                .onFailure { _error.value = it.message }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
