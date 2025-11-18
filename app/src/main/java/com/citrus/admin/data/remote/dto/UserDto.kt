package com.citrus.admin.data.remote.dto

import com.citrus.admin.data.local.entity.UserEntity

data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val status: String,
    val createdAt: Long,
    val lastLogin: Long?,
    val avatarUrl: String?
)

fun UserDto.toEntity() = UserEntity(
    id = id,
    name = name,
    email = email,
    role = role,
    status = status,
    createdAt = createdAt,
    lastLogin = lastLogin,
    avatarUrl = avatarUrl
)
