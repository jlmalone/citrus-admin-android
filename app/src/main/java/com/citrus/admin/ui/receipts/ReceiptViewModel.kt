package com.citrus.admin.ui.receipts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citrus.admin.data.local.entity.ReceiptEntity
import com.citrus.admin.data.repository.ReceiptRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReceiptUiState(
    val receipts: List<ReceiptEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedFilter: ReceiptFilter = ReceiptFilter.PENDING
)

enum class ReceiptFilter {
    ALL, PENDING, APPROVED, REJECTED
}

@HiltViewModel
class ReceiptViewModel @Inject constructor(
    private val receiptRepository: ReceiptRepository
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(ReceiptFilter.PENDING)
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    private val receiptsFlow: Flow<List<ReceiptEntity>> = _selectedFilter.flatMapLatest { filter ->
        when (filter) {
            ReceiptFilter.PENDING -> receiptRepository.getReceiptsByStatus("pending")
            ReceiptFilter.APPROVED -> receiptRepository.getReceiptsByStatus("approved")
            ReceiptFilter.REJECTED -> receiptRepository.getReceiptsByStatus("rejected")
            ReceiptFilter.ALL -> receiptRepository.getAllReceipts()
        }
    }

    val uiState: StateFlow<ReceiptUiState> = combine(
        receiptsFlow,
        _isLoading,
        _error,
        _selectedFilter
    ) { receipts, isLoading, error, filter ->
        ReceiptUiState(
            receipts = receipts,
            isLoading = isLoading,
            error = error,
            selectedFilter = filter
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReceiptUiState(isLoading = true)
    )

    init {
        syncReceipts()
    }

    fun onFilterChanged(filter: ReceiptFilter) {
        _selectedFilter.value = filter
    }

    fun syncReceipts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                receiptRepository.syncReceipts()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun approveReceipt(receiptId: String, notes: String? = null) {
        viewModelScope.launch {
            try {
                receiptRepository.reviewReceipt(receiptId, "approved", notes)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun rejectReceipt(receiptId: String, notes: String) {
        viewModelScope.launch {
            try {
                receiptRepository.reviewReceipt(receiptId, "rejected", notes)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
