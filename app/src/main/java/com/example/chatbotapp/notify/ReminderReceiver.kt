package com.example.chatbotapp.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.chatbotapp.R

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val task = intent.getStringExtra(EXTRA_TASK).orEmpty()
        val id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()

        ensureChannel(context)

        val notif = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Chatbot App Reminder")
            .setContentText(task.ifBlank { "It's time!" })
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(id, notif)
    }

    private fun ensureChannel(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val existing = nm.getNotificationChannel(CHANNEL_ID)
        if (existing == null) {
            val ch = NotificationChannel(
                CHANNEL_ID, "Chatbot App Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Time-based reminders from Chatbot App"
                enableLights(true); lightColor = Color.MAGENTA
                enableVibration(true)
            }
            nm.createNotificationChannel(ch)
        }
    }

    companion object {
        const val CHANNEL_ID = "ChatbotApp_reminders"
        const val EXTRA_TASK = "task"
        const val EXTRA_SCHEDULED_AT = "scheduled_at"
    }
}
