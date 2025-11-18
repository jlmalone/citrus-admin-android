package com.citrus.admin.data.repository

import com.citrus.admin.data.local.dao.UserDao
import com.citrus.admin.data.local.entity.UserEntity
import com.citrus.admin.data.remote.ApiService
import com.citrus.admin.data.remote.UpdateStatusRequest
import com.citrus.admin.data.remote.dto.toEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface UserRepository {
    fun getAllUsers(): Flow<List<UserEntity>>
    fun searchUsers(query: String): Flow<List<UserEntity>>
    fun getUsersByStatus(status: String): Flow<List<UserEntity>>
    fun getUserCount(): Flow<Int>
    fun getActiveUserCount(): Flow<Int>
    suspend fun syncUsers()
    suspend fun updateUserStatus(userId: String, status: String)
    suspend fun deleteUser(userId: String)
}

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val apiService: ApiService
) : UserRepository {

    override fun getAllUsers(): Flow<List<UserEntity>> =
        userDao.getAllUsers()

    override fun searchUsers(query: String): Flow<List<UserEntity>> =
        userDao.searchUsers(query)

    override fun getUsersByStatus(status: String): Flow<List<UserEntity>> =
        userDao.getUsersByStatus(status)

    override fun getUserCount(): Flow<Int> =
        userDao.getUserCount()

    override fun getActiveUserCount(): Flow<Int> =
        userDao.getActiveUserCount()

    override suspend fun syncUsers() {
        try {
            val users = apiService.getUsers().map { it.toEntity() }
            userDao.insertUsers(users)
        } catch (e: Exception) {
            // Handle error - in offline mode, we use cached data
            e.printStackTrace()
        }
    }

    override suspend fun updateUserStatus(userId: String, status: String) {
        try {
            val updated = apiService.updateUserStatus(userId, UpdateStatusRequest(status))
            userDao.updateUser(updated.toEntity())
        } catch (e: Exception) {
            // Update locally and sync later
            val user = userDao.getUserById(userId)
            user?.let {
                userDao.updateUser(it.copy(status = status))
            }
        }
    }

    override suspend fun deleteUser(userId: String) {
        try {
            apiService.deleteUser(userId)
            val user = userDao.getUserById(userId)
            user?.let { userDao.deleteUser(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
