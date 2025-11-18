package com.citrus.admin.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.citrus.admin.MainActivity
import com.citrus.admin.R
import com.citrus.admin.data.local.dao.AlertDao
import com.citrus.admin.data.local.entity.AlertEntity
import com.citrus.admin.data.remote.ApiService
import com.citrus.admin.data.remote.FcmTokenRequest
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class CitrusMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var alertDao: AlertDao

    @Inject
    lateinit var apiService: ApiService

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")

        // Send token to server
        serviceScope.launch {
            try {
                apiService.registerFcmToken(FcmTokenRequest(token))
            } catch (e: Exception) {
                Log.e(TAG, "Failed to register FCM token", e)
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "Message received from: ${message.from}")

        // Handle data payload
        message.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: ${message.data}")
            handleDataMessage(message.data)
        }

        // Handle notification payload
        message.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            showNotification(
                it.title ?: "Citrus Admin",
                it.body ?: "",
                message.data
            )
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val type = data["type"] ?: "info"
        val title = data["title"] ?: "System Alert"
        val message = data["message"] ?: ""
        val actionUrl = data["actionUrl"]

        // Save alert to database
        serviceScope.launch {
            try {
                val alert = AlertEntity(
                    id = "alert_${System.currentTimeMillis()}_${Random.nextInt()}",
                    type = type,
                    title = title,
                    message = message,
                    timestamp = System.currentTimeMillis(),
                    actionUrl = actionUrl
                )
                alertDao.insertAlert(alert)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save alert", e)
            }
        }

        // Show notification
        showNotification(title, message, data)
    }

    private fun showNotification(title: String, message: String, data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Add extra data if needed
            data.forEach { (key, value) ->
                putExtra(key, value)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "CitrusMessagingService"
    }
}
