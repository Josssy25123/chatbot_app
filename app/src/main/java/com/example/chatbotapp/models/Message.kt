package com.example.chatbotapp.models

data class Message(
    val role: String, // "user" or "assistant"
    val content: String
)
