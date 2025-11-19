package com.citrus.admin.data.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class UserTest {

    @Test
    fun `create user with default values`() {
        val user = User(
            id = "user123",
            email = "test@example.com",
            name = "Test User",
            role = UserRole.USER
        )

        assertThat(user.id).isEqualTo("user123")
        assertThat(user.email).isEqualTo("test@example.com")
        assertThat(user.name).isEqualTo("Test User")
        assertThat(user.role).isEqualTo(UserRole.USER)
        assertThat(user.isActive).isTrue()
        assertThat(user.createdAt).isGreaterThan(0)
    }

    @Test
    fun `create inactive user`() {
        val user = User(
            id = "user456",
            email = "inactive@example.com",
            name = "Inactive User",
            role = UserRole.MANAGER,
            isActive = false
        )

        assertThat(user.isActive).isFalse()
    }

    @Test
    fun `user roles are distinct`() {
        val admin = User("1", "admin@test.com", "Admin", UserRole.ADMIN)
        val manager = User("2", "manager@test.com", "Manager", UserRole.MANAGER)
        val user = User("3", "user@test.com", "User", UserRole.USER)

        assertThat(admin.role).isNotEqualTo(manager.role)
        assertThat(manager.role).isNotEqualTo(user.role)
        assertThat(user.role).isNotEqualTo(admin.role)
    }

    @Test
    fun `data class equality works correctly`() {
        val timestamp = System.currentTimeMillis()
        val user1 = User("1", "test@test.com", "Test", UserRole.USER, true, timestamp)
        val user2 = User("1", "test@test.com", "Test", UserRole.USER, true, timestamp)

        assertThat(user1).isEqualTo(user2)
    }

    @Test
    fun `data class copy works correctly`() {
        val user = User("1", "test@test.com", "Test", UserRole.USER)
        val updatedUser = user.copy(name = "Updated Name")

        assertThat(updatedUser.name).isEqualTo("Updated Name")
        assertThat(updatedUser.id).isEqualTo(user.id)
        assertThat(updatedUser.email).isEqualTo(user.email)
    }
}
