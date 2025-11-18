package com.citrus.admin.data.remote.dto

import com.citrus.admin.data.local.entity.AlertEntity

data class AlertDto(
    val id: String,
    val type: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val actionUrl: String?
)

fun AlertDto.toEntity() = AlertEntity(
    id = id,
    type = type,
    title = title,
    message = message,
    timestamp = timestamp,
    actionUrl = actionUrl
)
