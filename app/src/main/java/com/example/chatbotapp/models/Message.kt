package com.example.chatbotapp.models

import androidx.compose.ui.text.intl.Locale
import java.text.SimpleDateFormat

data class Message(
    val role: String, // "user" or "assistant"
    val content: String
)

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: String = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()),
    val hasAttachment: Boolean = false
)