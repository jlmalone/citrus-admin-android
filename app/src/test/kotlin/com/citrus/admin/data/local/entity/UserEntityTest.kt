package com.citrus.admin.data.local.entity

import com.citrus.admin.data.model.User
import com.citrus.admin.data.model.UserRole
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class UserEntityTest {

    @Test
    fun `convert User to UserEntity`() {
        val user = User(
            id = "user123",
            email = "test@example.com",
            name = "Test User",
            role = UserRole.ADMIN,
            isActive = true,
            createdAt = 1234567890L
        )

        val entity = UserEntity.fromUser(user)

        assertThat(entity.id).isEqualTo(user.id)
        assertThat(entity.email).isEqualTo(user.email)
        assertThat(entity.name).isEqualTo(user.name)
        assertThat(entity.role).isEqualTo(UserRole.ADMIN.name)
        assertThat(entity.isActive).isEqualTo(user.isActive)
        assertThat(entity.createdAt).isEqualTo(user.createdAt)
    }

    @Test
    fun `convert UserEntity to User`() {
        val entity = UserEntity(
            id = "user456",
            email = "entity@example.com",
            name = "Entity User",
            role = UserRole.MANAGER.name,
            isActive = false,
            createdAt = 9876543210L
        )

        val user = entity.toUser()

        assertThat(user.id).isEqualTo(entity.id)
        assertThat(user.email).isEqualTo(entity.email)
        assertThat(user.name).isEqualTo(entity.name)
        assertThat(user.role).isEqualTo(UserRole.MANAGER)
        assertThat(user.isActive).isEqualTo(entity.isActive)
        assertThat(user.createdAt).isEqualTo(entity.createdAt)
    }

    @Test
    fun `round trip conversion preserves data`() {
        val originalUser = User(
            id = "roundtrip123",
            email = "roundtrip@example.com",
            name = "Round Trip",
            role = UserRole.USER,
            isActive = true,
            createdAt = 5555555555L
        )

        val entity = UserEntity.fromUser(originalUser)
        val convertedUser = entity.toUser()

        assertThat(convertedUser).isEqualTo(originalUser)
    }

    @Test
    fun `all user roles can be converted`() {
        UserRole.values().forEach { role ->
            val user = User("id", "email", "name", role)
            val entity = UserEntity.fromUser(user)
            val convertedUser = entity.toUser()

            assertThat(convertedUser.role).isEqualTo(role)
        }
    }
}
