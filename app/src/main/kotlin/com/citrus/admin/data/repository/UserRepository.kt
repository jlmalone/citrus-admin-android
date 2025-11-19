package com.citrus.admin.data.repository

import com.citrus.admin.data.local.dao.UserDao
import com.citrus.admin.data.local.entity.UserEntity
import com.citrus.admin.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    fun getAllUsers(): Flow<List<User>> =
        userDao.getAllUsers().map { entities ->
            entities.map { it.toUser() }
        }

    fun getActiveUsers(): Flow<List<User>> =
        userDao.getActiveUsers().map { entities ->
            entities.map { it.toUser() }
        }

    suspend fun getUserById(userId: String): User? =
        userDao.getUserById(userId)?.toUser()

    suspend fun insertUser(user: User) {
        userDao.insertUser(UserEntity.fromUser(user))
    }

    suspend fun insertUsers(users: List<User>) {
        userDao.insertUsers(users.map { UserEntity.fromUser(it) })
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(UserEntity.fromUser(user))
    }

    suspend fun deleteUser(user: User) {
        userDao.deleteUser(UserEntity.fromUser(user))
    }

    suspend fun deleteUserById(userId: String) {
        userDao.deleteUserById(userId)
    }

    suspend fun getUserCount(): Int = userDao.getUserCount()

    suspend fun getActiveUserCount(): Int = userDao.getActiveUserCount()
}
