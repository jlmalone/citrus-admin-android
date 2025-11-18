package com.citrus.admin.data.remote

import com.citrus.admin.data.remote.dto.*
import retrofit2.http.*

interface ApiService {

    // User Management
    @GET("admin/users")
    suspend fun getUsers(): List<UserDto>

    @GET("admin/users/{id}")
    suspend fun getUserById(@Path("id") userId: String): UserDto

    @PUT("admin/users/{id}/status")
    suspend fun updateUserStatus(
        @Path("id") userId: String,
        @Body request: UpdateStatusRequest
    ): UserDto

    @DELETE("admin/users/{id}")
    suspend fun deleteUser(@Path("id") userId: String)

    // Receipt Management
    @GET("admin/receipts")
    suspend fun getReceipts(): List<ReceiptDto>

    @GET("admin/receipts/{id}")
    suspend fun getReceiptById(@Path("id") receiptId: String): ReceiptDto

    @PUT("admin/receipts/{id}/review")
    suspend fun reviewReceipt(
        @Path("id") receiptId: String,
        @Body request: ReviewReceiptRequest
    ): ReceiptDto

    // Analytics
    @GET("admin/analytics/summary")
    suspend fun getAnalyticsSummary(): AnalyticsSummaryDto

    @GET("admin/analytics/charts")
    suspend fun getAnalyticsCharts(
        @Query("period") period: String
    ): AnalyticsChartsDto

    // Alerts
    @GET("admin/alerts")
    suspend fun getAlerts(): List<AlertDto>

    @PUT("admin/alerts/{id}/dismiss")
    suspend fun dismissAlert(@Path("id") alertId: String)

    // FCM Token
    @POST("admin/fcm/token")
    suspend fun registerFcmToken(@Body request: FcmTokenRequest)
}

// Request/Response DTOs
data class UpdateStatusRequest(val status: String)
data class ReviewReceiptRequest(val status: String, val notes: String?)
data class FcmTokenRequest(val token: String)
