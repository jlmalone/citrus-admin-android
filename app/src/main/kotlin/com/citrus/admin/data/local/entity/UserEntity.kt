package com.citrus.admin.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.citrus.admin.data.model.User
import com.citrus.admin.data.model.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val name: String,
    val role: String,
    val isActive: Boolean,
    val createdAt: Long
) {
    fun toUser(): User = User(
        id = id,
        email = email,
        name = name,
        role = UserRole.valueOf(role),
        isActive = isActive,
        createdAt = createdAt
    )

    companion object {
        fun fromUser(user: User): UserEntity = UserEntity(
            id = user.id,
            email = user.email,
            name = user.name,
            role = user.role.name,
            isActive = user.isActive,
            createdAt = user.createdAt
        )
    }
}
