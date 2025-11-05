package com.example.chatbotapp.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.chatbotapp.notify.ReminderReceiver
import kotlinx.coroutines.tasks.await

class ReminderRepository(
    private val context: Context,
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    private fun getRemindersCollection() = auth.currentUser?.let { user ->
        db.collection("users")
            .document(user.uid)
            .collection("reminders")
    }

    /**
     * Save a new reminder in Firestore and schedule a local notification.
     */
    suspend fun addReminder(entry: ReminderEntry): Boolean {
        val collection = getRemindersCollection() ?: return false

        return try {
            collection.add(entry).await()
            scheduleReminder(entry)
            true
        } catch (e: Exception) {
            Log.e("ReminderRepository", "Error adding reminder", e)
            false
        }
    }

    /**
     * Retrieve all reminders for the current user.
     */
    suspend fun getAllReminders(): List<ReminderEntry> {
        val collection = getRemindersCollection() ?: return emptyList()

        return try {
            val snapshot = collection.get().await()
            snapshot.toObjects(ReminderEntry::class.java)
        } catch (e: Exception) {
            Log.e("ReminderRepository", "Error fetching reminders", e)
            emptyList()
        }
    }

    /**
     * Delete a reminder by its document ID.
     */
    suspend fun deleteReminder(reminderId: String): Boolean {
        val collection = getRemindersCollection() ?: return false

        return try {
            collection.document(reminderId).delete().await()
            true
        } catch (e: Exception) {
            Log.e("ReminderRepository", "Error deleting reminder", e)
            false
        }
    }

    /**
     * Schedules a reminder notification with AlarmManager.
     */
    private fun scheduleReminder(entry: ReminderEntry) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("reminder_message", entry.description)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            entry.title.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = entry.scheduleTime

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }

        Log.d("ReminderRepository", "Scheduled reminder: ${entry.title} at $triggerTime")
    }
}