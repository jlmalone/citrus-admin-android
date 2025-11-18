package com.citrus.admin.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citrus.admin.data.local.entity.UserEntity
import com.citrus.admin.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserUiState(
    val users: List<UserEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedFilter: UserFilter = UserFilter.ALL
)

enum class UserFilter {
    ALL, ACTIVE, INACTIVE
}

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedFilter = MutableStateFlow(UserFilter.ALL)
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    private val usersFlow: Flow<List<UserEntity>> = combine(
        _searchQuery,
        _selectedFilter
    ) { query, filter ->
        Pair(query, filter)
    }.flatMapLatest { (query, filter) ->
        when {
            query.isNotEmpty() -> userRepository.searchUsers(query)
            filter == UserFilter.ACTIVE -> userRepository.getUsersByStatus("active")
            filter == UserFilter.INACTIVE -> userRepository.getUsersByStatus("inactive")
            else -> userRepository.getAllUsers()
        }
    }

    val uiState: StateFlow<UserUiState> = combine(
        usersFlow,
        _isLoading,
        _error,
        _searchQuery,
        _selectedFilter
    ) { users, isLoading, error, query, filter ->
        UserUiState(
            users = users,
            isLoading = isLoading,
            error = error,
            searchQuery = query,
            selectedFilter = filter
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserUiState(isLoading = true)
    )

    init {
        syncUsers()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onFilterChanged(filter: UserFilter) {
        _selectedFilter.value = filter
    }

    fun syncUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                userRepository.syncUsers()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateUserStatus(userId: String, newStatus: String) {
        viewModelScope.launch {
            try {
                userRepository.updateUserStatus(userId, newStatus)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            try {
                userRepository.deleteUser(userId)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
