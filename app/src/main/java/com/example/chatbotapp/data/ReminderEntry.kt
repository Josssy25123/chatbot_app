package com.example.chatbotapp.data

data class ReminderEntry(
    val title: String,
    val description: String,
    val scheduleTime: Long,
    val createdAt: Long = System.currentTimeMillis()
)