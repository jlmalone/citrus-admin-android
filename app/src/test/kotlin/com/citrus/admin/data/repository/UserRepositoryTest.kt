package com.citrus.admin.data.repository

import app.cash.turbine.test
import com.citrus.admin.data.local.dao.UserDao
import com.citrus.admin.data.local.entity.UserEntity
import com.citrus.admin.data.model.User
import com.citrus.admin.data.model.UserRole
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UserRepositoryTest {

    private lateinit var userDao: UserDao
    private lateinit var repository: UserRepository

    @Before
    fun setup() {
        userDao = mockk(relaxed = true)
        repository = UserRepository(userDao)
    }

    @Test
    fun `getAllUsers returns mapped users`() = runTest {
        val entities = listOf(
            UserEntity("1", "user1@test.com", "User 1", "USER", true, 123L),
            UserEntity("2", "user2@test.com", "User 2", "ADMIN", true, 456L)
        )
        coEvery { userDao.getAllUsers() } returns flowOf(entities)

        repository.getAllUsers().test {
            val users = awaitItem()
            assertThat(users).hasSize(2)
            assertThat(users[0].id).isEqualTo("1")
            assertThat(users[1].id).isEqualTo("2")
            awaitComplete()
        }
    }

    @Test
    fun `getActiveUsers filters correctly`() = runTest {
        val entities = listOf(
            UserEntity("1", "active@test.com", "Active", "USER", true, 123L)
        )
        coEvery { userDao.getActiveUsers() } returns flowOf(entities)

        repository.getActiveUsers().test {
            val users = awaitItem()
            assertThat(users).hasSize(1)
            assertThat(users[0].isActive).isTrue()
            awaitComplete()
        }
    }

    @Test
    fun `getUserById returns correct user`() = runTest {
        val entity = UserEntity("123", "test@test.com", "Test", "MANAGER", true, 789L)
        coEvery { userDao.getUserById("123") } returns entity

        val user = repository.getUserById("123")

        assertThat(user).isNotNull()
        assertThat(user?.id).isEqualTo("123")
        assertThat(user?.role).isEqualTo(UserRole.MANAGER)
    }

    @Test
    fun `getUserById returns null when not found`() = runTest {
        coEvery { userDao.getUserById("nonexistent") } returns null

        val user = repository.getUserById("nonexistent")

        assertThat(user).isNull()
    }

    @Test
    fun `insertUser calls dao with correct entity`() = runTest {
        val user = User("1", "test@test.com", "Test", UserRole.USER)

        repository.insertUser(user)

        coVerify { userDao.insertUser(any()) }
    }

    @Test
    fun `insertUsers calls dao with multiple entities`() = runTest {
        val users = listOf(
            User("1", "user1@test.com", "User 1", UserRole.USER),
            User("2", "user2@test.com", "User 2", UserRole.ADMIN)
        )

        repository.insertUsers(users)

        coVerify { userDao.insertUsers(any()) }
    }

    @Test
    fun `updateUser calls dao`() = runTest {
        val user = User("1", "updated@test.com", "Updated", UserRole.MANAGER)

        repository.updateUser(user)

        coVerify { userDao.updateUser(any()) }
    }

    @Test
    fun `deleteUser calls dao`() = runTest {
        val user = User("1", "delete@test.com", "Delete", UserRole.USER)

        repository.deleteUser(user)

        coVerify { userDao.deleteUser(any()) }
    }

    @Test
    fun `deleteUserById calls dao with correct id`() = runTest {
        repository.deleteUserById("123")

        coVerify { userDao.deleteUserById("123") }
    }

    @Test
    fun `getUserCount returns correct count`() = runTest {
        coEvery { userDao.getUserCount() } returns 42

        val count = repository.getUserCount()

        assertThat(count).isEqualTo(42)
    }

    @Test
    fun `getActiveUserCount returns correct count`() = runTest {
        coEvery { userDao.getActiveUserCount() } returns 30

        val count = repository.getActiveUserCount()

        assertThat(count).isEqualTo(30)
    }
}
