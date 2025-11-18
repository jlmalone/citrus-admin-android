package com.citrus.admin.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citrus.admin.data.repository.AlertRepository
import com.citrus.admin.data.repository.ReceiptRepository
import com.citrus.admin.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    userRepository: UserRepository,
    receiptRepository: ReceiptRepository,
    alertRepository: AlertRepository
) : ViewModel() {

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

    val unreadAlertCount = alertRepository.getUnreadAlertCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
}
