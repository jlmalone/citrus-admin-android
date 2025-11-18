package com.citrus.admin.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val status: String, // "active" or "inactive"
    val createdAt: Long,
    val lastLogin: Long?,
    val avatarUrl: String?,
    val syncedAt: Long = System.currentTimeMillis()
)
