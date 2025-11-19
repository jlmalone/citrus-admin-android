package com.citrus.admin.ui.users

import app.cash.turbine.test
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.citrus.admin.data.local.entity.UserEntity
import com.citrus.admin.data.repository.UserRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: UserRepository
    private lateinit var viewModel: UserViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        coEvery { repository.getAllUsers() } returns flowOf(emptyList())
        coEvery { repository.syncUsers() } returns Unit

        viewModel = UserViewModel(repository)

        viewModel.uiState.test {
            val initialState = awaitItem()
            assertThat(initialState.isLoading).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getAllUsers returns user list`() = runTest {
        val users = listOf(
            UserEntity("1", "user1@test.com", "User 1", "active", 123L),
            UserEntity("2", "user2@test.com", "User 2", "active", 456L)
        )
        coEvery { repository.getAllUsers() } returns flowOf(users)
        coEvery { repository.syncUsers() } returns Unit

        viewModel = UserViewModel(repository)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.users).hasSize(2)
            assertThat(state.users[0].email).isEqualTo("user1@test.com")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSearchQueryChanged updates search query`() = runTest {
        coEvery { repository.getAllUsers() } returns flowOf(emptyList())
        coEvery { repository.searchUsers(any()) } returns flowOf(emptyList())
        coEvery { repository.syncUsers() } returns Unit

        viewModel = UserViewModel(repository)
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("test query")
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.searchQuery).isEqualTo("test query")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onFilterChanged updates filter`() = runTest {
        coEvery { repository.getAllUsers() } returns flowOf(emptyList())
        coEvery { repository.getUsersByStatus(any()) } returns flowOf(emptyList())
        coEvery { repository.syncUsers() } returns Unit

        viewModel = UserViewModel(repository)
        advanceUntilIdle()

        viewModel.onFilterChanged(UserFilter.ACTIVE)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.selectedFilter).isEqualTo(UserFilter.ACTIVE)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `syncUsers calls repository sync`() = runTest {
        coEvery { repository.getAllUsers() } returns flowOf(emptyList())
        coEvery { repository.syncUsers() } returns Unit

        viewModel = UserViewModel(repository)
        advanceUntilIdle()

        viewModel.syncUsers()
        advanceUntilIdle()

        coVerify(atLeast = 2) { repository.syncUsers() } // Once in init, once manually
    }

    @Test
    fun `syncUsers handles errors gracefully`() = runTest {
        coEvery { repository.getAllUsers() } returns flowOf(emptyList())
        coEvery { repository.syncUsers() } throws Exception("Network error")

        viewModel = UserViewModel(repository)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo("Network error")
            assertThat(state.isLoading).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateUserStatus calls repository`() = runTest {
        coEvery { repository.getAllUsers() } returns flowOf(emptyList())
        coEvery { repository.syncUsers() } returns Unit
        coEvery { repository.updateUserStatus(any(), any()) } returns Unit

        viewModel = UserViewModel(repository)
        advanceUntilIdle()

        viewModel.updateUserStatus("user123", "inactive")
        advanceUntilIdle()

        coVerify { repository.updateUserStatus("user123", "inactive") }
    }

    @Test
    fun `deleteUser calls repository`() = runTest {
        coEvery { repository.getAllUsers() } returns flowOf(emptyList())
        coEvery { repository.syncUsers() } returns Unit
        coEvery { repository.deleteUser(any()) } returns Unit

        viewModel = UserViewModel(repository)
        advanceUntilIdle()

        viewModel.deleteUser("user123")
        advanceUntilIdle()

        coVerify { repository.deleteUser("user123") }
    }

    @Test
    fun `clearError resets error state`() = runTest {
        coEvery { repository.getAllUsers() } returns flowOf(emptyList())
        coEvery { repository.syncUsers() } throws Exception("Error")

        viewModel = UserViewModel(repository)
        advanceUntilIdle()

        viewModel.clearError()
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isNull()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filter ACTIVE queries active users`() = runTest {
        coEvery { repository.getAllUsers() } returns flowOf(emptyList())
        coEvery { repository.getUsersByStatus("active") } returns flowOf(emptyList())
        coEvery { repository.syncUsers() } returns Unit

        viewModel = UserViewModel(repository)
        advanceUntilIdle()

        viewModel.onFilterChanged(UserFilter.ACTIVE)
        advanceUntilIdle()

        coVerify { repository.getUsersByStatus("active") }
    }

    @Test
    fun `search query triggers search`() = runTest {
        coEvery { repository.getAllUsers() } returns flowOf(emptyList())
        coEvery { repository.searchUsers("john") } returns flowOf(emptyList())
        coEvery { repository.syncUsers() } returns Unit

        viewModel = UserViewModel(repository)
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("john")
        advanceUntilIdle()

        coVerify { repository.searchUsers("john") }
    }
}
