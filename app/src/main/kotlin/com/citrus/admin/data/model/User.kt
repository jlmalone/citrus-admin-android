package com.citrus.admin.data.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val role: UserRole,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

enum class UserRole {
    ADMIN,
    MANAGER,
    USER
}
