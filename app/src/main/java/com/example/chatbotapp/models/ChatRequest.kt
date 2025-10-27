package com.example.chatbotapp.models

data class ChatRequest(
    val model: String,
    val messages: List<Message>
)
